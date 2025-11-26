package domain;

import domain.ManejadorMapaInfinito;
import model.GameConfig;
import model.EnemigoModel;
import model.ConsumableItemModel;
import model.Acertijo;
import infrastructure.InputService;
import infrastructure.AcertijosLoader;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import infrastructure.ResourceLoader;
import infrastructure.networking.GameClient;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameEngine implements IUpdateable {
    private JugadorSystem jugadorSystem;
    private CamaraSystem camaraSystem;
    private EnemigoSystem enemigoSystem;
    private ManejadorMapaInfinito mapaInfinito;
    private MapaInfinitoAdapter mapaAdapter;
    private GameConfig config;
    private InputService inputService;
    private boolean jugando;
    private boolean modalCofreActivo = false;
    private boolean modalRespuestaCorrectaActivo = false;
    private boolean modalRespuestaIncorrectaActivo = false;
    private boolean cofreYaActivado = false;
    private boolean cofreBloqueado = false;
    private AcertijosLoader acertijosLoader;
    private Acertijo acertijoActual;
    private int cofreTileX = -1;
    private int cofreTileY = -1;
    
    // Multiplayer
    private Map<String, RemotePlayer> remotePlayers = new ConcurrentHashMap<>();
    private int lastX = -1, lastY = -1;
    private String lastDir = "";
    
    // Timer variables
    private static final long TIEMPO_LIMITE_MS = 3 * 60 * 1000; // 3 minutos en milisegundos
    private long tiempoInicioJuego;
    private long tiempoTranscurrido;
    private boolean juegoTerminado;

    public GameEngine(GameConfig config, 
                      JugadorSystem jugadorSystem, 
                      CamaraSystem camaraSystem,
                      ManejadorMapaInfinito mapaInfinito,
                      EnemigoSystem enemigoSystem,
                      InputService inputService) {
        this.config = config;
        this.jugadorSystem = jugadorSystem;
        this.camaraSystem = camaraSystem;
        this.mapaInfinito = mapaInfinito;
        this.mapaAdapter = null;
        this.enemigoSystem = enemigoSystem;
        this.inputService = inputService;
        this.jugando = true;
        this.acertijosLoader = new AcertijosLoader("data/data.json");
        
        inicializar();
    }

    public GameEngine(GameConfig config, 
                      JugadorSystem jugadorSystem, 
                      CamaraSystem camaraSystem,
                      MapaInfinitoAdapter mapaAdapter,
                      EnemigoSystem enemigoSystem,
                      InputService inputService) {
        this.config = config;
        this.jugadorSystem = jugadorSystem;
        this.camaraSystem = camaraSystem;
        this.mapaAdapter = mapaAdapter;
        this.mapaInfinito = mapaAdapter.getMapaOriginal();
        this.enemigoSystem = enemigoSystem;
        this.inputService = inputService;
        this.jugando = true;
        this.acertijosLoader = new AcertijosLoader("data/data.json");
        
        inicializar();
    }

    private void inicializar() {
        camaraSystem.seguirEntidad(
            jugadorSystem.getMundoX(), 
            jugadorSystem.getMundoY()
        );
        mapaInfinito.actualizarChunksActivos(
            jugadorSystem.getMundoX(), 
            jugadorSystem.getMundoY()
        );
        
        enemigoSystem.generarEnemigosIniciales(
            jugadorSystem.getMundoX(), 
            jugadorSystem.getMundoY(), 
            5
        );
        
        // NO inicializar timer aquí - se iniciará cuando comience la partida
        tiempoInicioJuego = 0;
        tiempoTranscurrido = 0;
        juegoTerminado = false;
    }

    @Override
    public void update() {
        if (modalCofreActivo) {
            verificarRespuesta();
            return; 
        }

        if (modalRespuestaCorrectaActivo) {
            if (inputService.isTeclaEnter()) {
                modalRespuestaCorrectaActivo = false;
                inputService.setTeclaEnter(false);
            }
            return;
        }

        if (modalRespuestaIncorrectaActivo) {
            if (inputService.isTeclaEnter()) {
                modalRespuestaIncorrectaActivo = false;
                inputService.setTeclaEnter(false);
                if (acertijoActual != null && acertijoActual.tieneIntentosDisponibles()) {
                    modalCofreActivo = true;
                } else {
                    cofreBloqueado = true;
                }
            }
            return;
        }

        if (!jugando) return;
        
        // Iniciar timer si aún no ha comenzado
        if (tiempoInicioJuego == 0) {
            tiempoInicioJuego = System.currentTimeMillis();
        }
        
        // Actualizar timer
        tiempoTranscurrido = System.currentTimeMillis() - tiempoInicioJuego;
        if (tiempoTranscurrido >= TIEMPO_LIMITE_MS && !juegoTerminado) {
            juegoTerminado = true;
            jugando = false;
            return;
        }
        
        jugadorSystem.update();

        int tileColision = jugadorSystem.getMovimientoSystem().getUltimaColision();
        if (tileColision == GeneradorMundo.TILE_COFRE) {
            if (!cofreYaActivado && !cofreBloqueado) {
                if (acertijoActual == null) {
                    this.acertijoActual = acertijosLoader.obtenerAcertijoAleatorio();
                }
                
                int jugadorTileX = jugadorSystem.getMundoX() / config.getTamanioTile();
                int jugadorTileY = jugadorSystem.getMundoY() / config.getTamanioTile();
                
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int checkX = jugadorTileX + dx;
                        int checkY = jugadorTileY + dy;
                        if (getMapa().getTileEnMundo(checkX, checkY) == GeneradorMundo.TILE_COFRE) {
                            cofreTileX = checkX;
                            cofreTileY = checkY;
                            break;
                        }
                    }
                }
                
                this.modalCofreActivo = true;
                this.cofreYaActivado = true;
            }
        } else {
            this.cofreYaActivado = false;
            if (cofreBloqueado) {
                this.cofreBloqueado = false;
                this.acertijoActual = null;
                cofreTileX = -1;
                cofreTileY = -1;
            }
        }

        enemigoSystem.update(jugadorSystem);
        checkDamageCollisions(); 
        checkItemCollisions(); 

        
        camaraSystem.seguirEntidad(
            jugadorSystem.getMundoX(), 
            jugadorSystem.getMundoY()
        );
        
        if (mapaAdapter != null) {
            mapaAdapter.actualizarChunksActivos(
                jugadorSystem.getMundoX(), 
                jugadorSystem.getMundoY()
            );
        } else if (mapaInfinito != null) {
            mapaInfinito.actualizarChunksActivos(
                jugadorSystem.getMundoX(), 
                jugadorSystem.getMundoY()
            );
        }
    }

    private void verificarRespuesta() {
        String respuestaSeleccionada = null;
        if (inputService.isTeclaA()) {
            respuestaSeleccionada = "A";
        } else if (inputService.isTeclaB()) {
            respuestaSeleccionada = "B";
        } else if (inputService.isTeclaC()) {
            respuestaSeleccionada = "C";
        } else if (inputService.isTeclaD()) {
            respuestaSeleccionada = "D";
        }

        if (respuestaSeleccionada != null && acertijoActual != null) {
            modalCofreActivo = false;
            
            if (respuestaSeleccionada.equals(acertijoActual.getRespuestaCorrecta())) {
                modalRespuestaCorrectaActivo = true;
                cofreBloqueado = true;
                jugadorSystem.incrementarAcertijosResueltos();
                
                if (cofreTileX != -1 && cofreTileY != -1) {
                    getMapa().setTileEnMundo(cofreTileX, cofreTileY, GeneradorMundo.TILE_COFRE_CERRADO);
                }
            } else {
                acertijoActual.decrementarIntentos();
                modalRespuestaIncorrectaActivo = true;
            }
        }
    }

    public boolean isModalCofreActivo() {
        return modalCofreActivo;
    }

    public boolean isModalRespuestaCorrectaActivo() {
        return modalRespuestaCorrectaActivo;
    }

    public boolean isModalRespuestaIncorrectaActivo() {
        return modalRespuestaIncorrectaActivo;
    }

    public void setModalCofreActivo(boolean modalCofreActivo) {
        this.modalCofreActivo = modalCofreActivo;
    }

    public Acertijo getAcertijoActual() {
        return acertijoActual;
    }

    public JugadorSystem getJugadorSystem() {
        return jugadorSystem;
    }

    public CamaraSystem getCamaraSystem() {
        return camaraSystem;
    }

    public EnemigoSystem getEnemigoSystem() {
        return enemigoSystem;
    }

    public ManejadorMapaInfinito getMapaInfinito() {
        return mapaInfinito;
    }

    private ManejadorMapaInfinito getMapa() {
        return mapaAdapter != null ? mapaAdapter.getMapaOriginal() : mapaInfinito;
    }

    public boolean isJugando() {
        return jugando;
    }

    public boolean isPausa() {
        return !jugando;
    }

    public void setJugando(boolean jugando) {
        this.jugando = jugando;
    }

    public void pausar() {
        this.jugando = false;
    }

    public void reanudar() {
        this.jugando = true;
    }

    public void reiniciarJuego() {
        // Reiniciar estado del jugador
        jugadorSystem.getJugador().setVida(100);
        jugadorSystem.setPocionesEnArsenal(0);
        jugadorSystem.setAcertijosResueltos(0);
        jugadorSystem.reiniciarCooldownDanio();
        
        // Reiniciar posición del jugador
        jugadorSystem.getMovimientoSystem().setMundoX(5000);
        jugadorSystem.getMovimientoSystem().setMundoY(5000);
        
        // Reiniciar modales y estados de cofre
        modalCofreActivo = false;
        modalRespuestaCorrectaActivo = false;
        modalRespuestaIncorrectaActivo = false;
        cofreYaActivado = false;
        cofreBloqueado = false;
        acertijoActual = null;
        cofreTileX = -1;
        cofreTileY = -1;
        
        // Reiniciar enemigos
        enemigoSystem.getEnemigos().clear();
        enemigoSystem.generarEnemigosIniciales(
            jugadorSystem.getMundoX(), 
            jugadorSystem.getMundoY(), 
            5
        );
        
        // REGENERAR MAPA CON NUEVO SEED
        long nuevoSeed = System.currentTimeMillis();
        mapaInfinito.regenerarConNuevoSeed(nuevoSeed);
        
        // Reiniciar cámara y mapa
        camaraSystem.seguirEntidad(
            jugadorSystem.getMundoX(), 
            jugadorSystem.getMundoY()
        );
        
        if (mapaAdapter != null) {
            mapaAdapter.actualizarChunksActivos(
                jugadorSystem.getMundoX(), 
                jugadorSystem.getMundoY()
            );
        } else if (mapaInfinito != null) {
            mapaInfinito.actualizarChunksActivos(
                jugadorSystem.getMundoX(), 
                jugadorSystem.getMundoY()
            );
        }
        
        // Reiniciar timer
        tiempoInicioJuego = System.currentTimeMillis();
        tiempoTranscurrido = 0;
        juegoTerminado = false;
        
        jugando = true;
    }

    private void checkDamageCollisions() {
        int margen = 8;
        int anchoCuerpo = config.getTamanioTile() - 2 * margen;
        int altoCuerpo = config.getTamanioTile() - 2 * margen;

        int jugadorX = jugadorSystem.getMundoX();
        int jugadorY = jugadorSystem.getMundoY();

        for (EnemigoModel enemigo : enemigoSystem.getEnemigos()) {
            int enemigoX = enemigo.getTransform().getX();
            int enemigoY = enemigo.getTransform().getY();

            if (ColisionSystem.verificarColisionEntidades(
                    jugadorX + margen, jugadorY + margen, anchoCuerpo, altoCuerpo,
                    enemigoX + margen, enemigoY + margen, anchoCuerpo, altoCuerpo)) {
                
                jugadorSystem.recibirDanio(enemigo.getDamage());
            }
        }
    }

    private void checkItemCollisions() {
        int margen = 8;
        int anchoCuerpo = config.getTamanioTile() - 2 * margen;
        int altoCuerpo = config.getTamanioTile() - 2 * margen;

        int jugadorX = jugadorSystem.getMundoX();
        int jugadorY = jugadorSystem.getMundoY();

        for (ConsumableItemModel item : new ArrayList<>(mapaInfinito.getItemsConsumibles())) {
            if (!item.isPickedUp()) {
                int itemX = item.getX();
                int itemY = item.getY();

                if (ColisionSystem.verificarColisionEntidades(
                        jugadorX + margen, jugadorY + margen, anchoCuerpo, altoCuerpo,
                        itemX + margen, itemY + margen, anchoCuerpo, altoCuerpo)) {
                    
                    jugadorSystem.recogerItem(item);
                    item.setPickedUp(true); // Marcar como recogido
                }
            }
        }
    }
    
    public long getTiempoRestanteMs() {
        long restante = TIEMPO_LIMITE_MS - tiempoTranscurrido;
        return Math.max(0, restante);
    }
    
    public int getTiempoRestanteSegundos() {
        return (int) (getTiempoRestanteMs() / 1000);
    }

    public void updateMultiplayer(GameClient client) {
        if (client == null || !client.isConnected()) return;

        // 1. Procesar mensajes recibidos
        String msg;
        while ((msg = client.getNextMessage()) != null) {
            if (msg.startsWith("POS:")) {
                // Formato: POS:id:x:y:dir
                String[] parts = msg.split(":");
                if (parts.length >= 5) {
                    String id = parts[1];
                    // Ignorar mi propia posición si llegara a rebotar
                    if (client.getMyId() != null && id.equals(client.getMyId())) continue;
                    
                    try {
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        String dir = parts[4];
                        
                        remotePlayers.computeIfAbsent(id, k -> new RemotePlayer(k, x, y))
                                     .updatePosition(x, y, dir);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parseando posición: " + msg);
                    }
                }
            }
        }

        // 2. Enviar mi posición si ha cambiado
        int currentX = jugadorSystem.getMundoX();
        int currentY = jugadorSystem.getMundoY();
        String currentDir = jugadorSystem.getJugador().getDireccion();

        if (currentX != lastX || currentY != lastY || !currentDir.equals(lastDir)) {
            client.sendPosition(currentX, currentY, currentDir);
            lastX = currentX;
            lastY = currentY;
            lastDir = currentDir;
        }
    }

    public Map<String, RemotePlayer> getRemotePlayers() {
        return remotePlayers;
    }
}
    
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
}
