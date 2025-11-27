package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import Presentation.RenderSystem;
import domain.GameEngine;
import infrastructure.ConfigManager;
import infrastructure.InputService;
import infrastructure.networking.GameClient;
import infrastructure.networking.GameServer;
import model.GameConfig;

public class GamePanel extends JPanel implements Runnable {
    public enum GameState {
        BIENVENIDA,
        MENU_PRINCIPAL,
        MENU_COMANDOS,
        JUGANDO,
        PAUSA,
        MENU_PAUSA,
        MODAL_COFRE,
        MODAL_RESPUESTA_CORRECTA,
        MODAL_RESPUESTA_INCORRECTA,
        CREAR_SERVIDOR,
        SALA_ESPERA_HOST,
        UNIRSE_SERVIDOR,
        SALA_ESPERA_CLIENTE,
        JUEGO_TERMINADO
    }

    private GameState estadoJuego;
    private GameConfig config;
    private GameEngine gameEngine;
    private RenderSystem renderSystem;
    private InputService inputService;
    private Thread hebraJuego;
    
    private GameServer server;
    private GameClient client;
    
    private String serverID = "";
    private String inputServerID = "";
    private int usuariosConectados = 0;
    private boolean mostrarMensajeCopiado = false;
    private long tiempoMensajeCopiado = 0;

    public GamePanel(GameEngine gameEngine, InputService inputService) {
        this.config = ConfigManager.getInstance().getGameConfig();
        this.gameEngine = gameEngine;
        this.inputService = inputService;
        this.renderSystem = new RenderSystem(config.getTamanioTile());
        this.estadoJuego = GameState.BIENVENIDA;

        configurarPanel();
    }

    private void configurarPanel() {
        this.setPreferredSize(new Dimension(config.getAnchoPantalla(), config.getAltoPantalla()));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(inputService);
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                manejarInputTexto(e);
            }
        });
        this.setFocusable(true);
    }

    public void iniciarJuego() {
        if (hebraJuego == null) {
            hebraJuego = new Thread(this);
            hebraJuego.start();
        }
    }

    public void detenerJuego() {
        if (hebraJuego != null) {
            try {
                hebraJuego.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        double intervaloDibujo = 1000000000.0 / config.getFps();
        double delta = 0;
        long ultimaVez = System.nanoTime();
        long tiempoActual;

        while (hebraJuego != null) {
            tiempoActual = System.nanoTime();
            delta += (tiempoActual - ultimaVez) / intervaloDibujo;
            ultimaVez = tiempoActual;

            if (delta >= 1) {
                actualizar();
                repaint();
                delta--;
            }
        }
    }

    private void actualizar() {
        switch (estadoJuego) {
            case BIENVENIDA:
                if (inputService.isTeclaEnter()) {
                    estadoJuego = GameState.MENU_PRINCIPAL;
                    inputService.setTeclaEnter(false);
                }
                break;
            case MENU_PRINCIPAL:
                if (inputService.isTecla1()) {
                    estadoJuego = GameState.JUGANDO;
                    inputService.setTecla1(false);
                } else if (inputService.isTecla2()) {
                    estadoJuego = GameState.CREAR_SERVIDOR;
                    resetearInputs();
                    inputService.setTecla2(false);
                } else if (inputService.isTecla3()) {
                    estadoJuego = GameState.UNIRSE_SERVIDOR;
                    resetearInputs();
                    inputService.setTecla3(false);
                } else if (inputService.isTecla4()) {
                    estadoJuego = GameState.MENU_COMANDOS;
                    inputService.setTecla4(false);
                } else if (inputService.isTecla5()) {
                    System.exit(0);
                }
                break;
            case MENU_COMANDOS:
                if (inputService.isTeclaEscape()) {
                    estadoJuego = GameState.MENU_PRINCIPAL;
                    inputService.setTeclaEscape(false);
                }
                break;
            case JUGANDO:
                if (inputService.isTeclaEscape()) {
                    estadoJuego = GameState.MENU_PAUSA;
                    inputService.setTeclaEscape(false);
                } else {
                    gameEngine.update();
                    
                    // Actualizar multijugador (siempre, incluso sin conexión)
                    gameEngine.updateMultiplayer(client);
                    
                    // Verificar si el servidor fue cerrado o conexión perdida
                    if (client != null && !client.isConnected()) {
                        System.out.println("Conexión con servidor perdida");
                        estadoJuego = GameState.MENU_PRINCIPAL;
                    } else if (gameEngine.isJuegoTerminado()) {
                        estadoJuego = GameState.JUEGO_TERMINADO;
                    } else if (gameEngine.isPausa()) {
                        estadoJuego = GameState.PAUSA;
                    } else if (gameEngine.isModalCofreActivo()) {
                        estadoJuego = GameState.MODAL_COFRE;
                    }
                }
                break;
            case PAUSA:
                if (!gameEngine.isPausa()) {
                    estadoJuego = GameState.JUGANDO;
                }
                break;
            case MENU_PAUSA:
                if (inputService.isTecla1()) {
                    estadoJuego = GameState.JUGANDO;
                    inputService.setTecla1(false);
                } else if (inputService.isTecla2()) {
                    gameEngine.reiniciarJuego();
                    estadoJuego = GameState.JUGANDO;
                    inputService.setTecla2(false);
                } else if (inputService.isTecla3()) {
                    gameEngine.reiniciarJuego();
                    estadoJuego = GameState.MENU_PRINCIPAL;
                    inputService.setTecla3(false);
                } else if (inputService.isTeclaEscape()) {
                    estadoJuego = GameState.JUGANDO;
                    inputService.setTeclaEscape(false);
                }
                break;
            case MODAL_COFRE:
                gameEngine.update();
                if (gameEngine.isModalRespuestaCorrectaActivo()) {
                    estadoJuego = GameState.MODAL_RESPUESTA_CORRECTA;
                } else if (gameEngine.isModalRespuestaIncorrectaActivo()) {
                    estadoJuego = GameState.MODAL_RESPUESTA_INCORRECTA;
                } else if (!gameEngine.isModalCofreActivo()) {
                    estadoJuego = GameState.JUGANDO;
                }
                break;
            case MODAL_RESPUESTA_CORRECTA:
                gameEngine.update();
                if (!gameEngine.isModalRespuestaCorrectaActivo()) {
                    estadoJuego = GameState.JUGANDO;
                }
                break;
            case MODAL_RESPUESTA_INCORRECTA:
                gameEngine.update();
                if (!gameEngine.isModalRespuestaIncorrectaActivo()) {
                    estadoJuego = GameState.JUGANDO;
                }
                break;
            case CREAR_SERVIDOR:
                if (inputService.isTeclaEnter()) {
                    if (server == null) {
                        try {
                            server = new GameServer(8888);
                            server.start();
                            serverID = server.getServerId();
                            
                            // El HOST también se conecta a sí mismo como cliente para sincronizar
                            client = new GameClient("localhost", 8888);
                            if (client.connect()) {
                                System.out.println("HOST conectado a sí mismo como cliente");
                            } else {
                                System.err.println("ERROR: El HOST no pudo conectarse a sí mismo");
                                client = null;
                            }
                        } catch (Exception e) {
                            System.err.println("ERROR CRÍTICO AL INICIAR SERVIDOR: " + e.getMessage());
                            e.printStackTrace();
                            serverID = "ERROR-PUERTO";
                            server = null;
                            client = null;
                        }
                    }
                    estadoJuego = GameState.SALA_ESPERA_HOST;
                    usuariosConectados = 1;
                    inputService.setTeclaEnter(false);
                } else if (inputService.isTeclaEscape()) {
                    if (server != null) {
                        server.stop();
                        server = null;
                    }
                    if (client != null) {
                        client.disconnect();
                        client = null;
                    }
                    estadoJuego = GameState.MENU_PRINCIPAL;
                    inputService.setTeclaEscape(false);
                }
                break;
            case SALA_ESPERA_HOST:
                if (server != null) {
                    usuariosConectados = 1 + server.getConnectedPlayers();
                }
                if (inputService.isTeclaEnter()) {
                    if (server != null) {
                        // Enviar seed del mapa antes de iniciar
                        long seed = gameEngine.getSeed();
                        server.broadcast("MAP_SEED:" + seed, null);
                        server.broadcast("START_GAME", null);
                    }
                    estadoJuego = GameState.JUGANDO;
                    inputService.setTeclaEnter(false);
                } else if (inputService.isTeclaC()) {
                    copiarAlPortapapeles(serverID);
                    mostrarMensajeCopiado = true;
                    tiempoMensajeCopiado = System.currentTimeMillis();
                    inputService.setTeclaC(false);
                } else if (inputService.isTeclaEscape()) {
                    if (server != null) {
                        server.stop();
                        server = null;
                        System.out.println("Servidor detenido y sala cerrada por el usuario.");
                    }
                    estadoJuego = GameState.MENU_PRINCIPAL;
                    inputService.setTeclaEscape(false);
                }
                
                if (mostrarMensajeCopiado && (System.currentTimeMillis() - tiempoMensajeCopiado) > 2000) {
                    mostrarMensajeCopiado = false;
                }
                break;
            case UNIRSE_SERVIDOR:
                if (inputService.isTeclaEnter() && !inputServerID.isEmpty()) {
                    String targetIp = inputServerID.trim();
                    if (!targetIp.contains(".")) {
                        targetIp = "localhost";
                    }
                    // Limpiar posibles caracteres invisibles o espacios
                    targetIp = targetIp.replaceAll("\\s+", "");
                    
                    client = new GameClient(targetIp, 8888);
                    if (client.connect()) {
                        estadoJuego = GameState.SALA_ESPERA_CLIENTE;
                        usuariosConectados = 2;
                    }
                    inputService.setTeclaEnter(false);
                } else if (inputService.isTeclaEscape()) {
                    estadoJuego = GameState.MENU_PRINCIPAL;
                    inputService.setTeclaEscape(false);
                }
                break;
            case SALA_ESPERA_CLIENTE:
                if (client != null) {                    
                    String msg;
                    while ((msg = client.getNextMessage()) != null) {
                        if (msg.startsWith("MAP_SEED:")) {
                            try {
                                long seed = Long.parseLong(msg.split(":")[1]);
                                gameEngine.reiniciarConSeed(seed);
                                System.out.println("Mapa reiniciado con nueva seed: " + seed);
                            } catch (Exception e) {
                                System.err.println("Error seed: " + e.getMessage());
                            }
                        } else if (msg.equals("START_GAME")) {
                            estadoJuego = GameState.JUGANDO;
                        }
                    }
                }
                
                if (inputService.isTeclaEscape()) {
                    if (client != null) {
                        client.disconnect();
                        client = null;
                    }
                    estadoJuego = GameState.MENU_PRINCIPAL;
                    inputService.setTeclaEscape(false);
                }
                break;
            case JUEGO_TERMINADO:
                if (inputService.isTeclaEnter()) {
                    gameEngine.reiniciarJuego();
                    estadoJuego = GameState.JUGANDO;
                    inputService.setTeclaEnter(false);
                } else if (inputService.isTeclaEscape()) {
                    gameEngine.reiniciarJuego();
                    estadoJuego = GameState.MENU_PRINCIPAL;
                    inputService.setTeclaEscape(false);
                }
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch (estadoJuego) {
            case BIENVENIDA:
                renderSystem.renderBienvenida(g2, config.getAnchoPantalla(), config.getAltoPantalla());
                break;
            case MENU_PRINCIPAL:
                renderSystem.renderMenuPrincipal(g2, config.getAnchoPantalla(), config.getAltoPantalla());
                break;
            case MENU_COMANDOS:
                renderSystem.renderMenuComandos(g2, config.getAnchoPantalla(), config.getAltoPantalla());
                break;
            case CREAR_SERVIDOR:
                renderSystem.renderCrearServidor(g2, config.getAnchoPantalla(), config.getAltoPantalla());
                break;
            case SALA_ESPERA_HOST:
                renderSystem.renderSalaEsperaHost(g2, config.getAnchoPantalla(), config.getAltoPantalla(), 
                    usuariosConectados, serverID, mostrarMensajeCopiado);
                break;
            case UNIRSE_SERVIDOR:
                renderSystem.renderUnirseServidor(g2, config.getAnchoPantalla(), config.getAltoPantalla(), inputServerID);
                break;
            case SALA_ESPERA_CLIENTE:
                renderSystem.renderSalaEsperaCliente(g2, config.getAnchoPantalla(), config.getAltoPantalla(), usuariosConectados);
                break;
            case JUEGO_TERMINADO:
                renderSystem.renderJuegoTerminado(g2, config.getAnchoPantalla(), config.getAltoPantalla(), 
                    gameEngine.getJugadorSystem(),
                    gameEngine.getStatsLocal(),
                    gameEngine.getRemotePlayers());
                break;
            case JUGANDO:
            case PAUSA:
            case MENU_PAUSA:
            case MODAL_COFRE:
            case MODAL_RESPUESTA_CORRECTA:
            case MODAL_RESPUESTA_INCORRECTA:
                renderSystem.renderTodo(
                    g2,
                    gameEngine.getMapaInfinito(),
                    gameEngine.getCamaraSystem(),
                    gameEngine.getJugadorSystem(),
                    gameEngine.getEnemigoSystem(),
                    gameEngine.getRemotePlayers(),
                    config.getAnchoPantalla(),
                    config.getAltoPantalla(),
                    gameEngine.getTiempoRestanteSegundos()
                );

                // Renderizar HUD multijugador (siempre, incluso en singleplayer)
                renderSystem.renderMultiplayerHUD(g2,
                    gameEngine.getStatsLocal(),
                    gameEngine.getRemotePlayers(),
                    config.getAnchoPantalla(),
                    config.getAltoPantalla(),
                    (client != null && client.isConnected()),
                    (client != null && client.isConnected()) 
                );

                if (estadoJuego == GameState.PAUSA) {
                    renderSystem.renderPausa(g2, config.getAnchoPantalla(), config.getAltoPantalla());
                } else if (estadoJuego == GameState.MENU_PAUSA) {
                    renderSystem.renderMenuPausa(g2, config.getAnchoPantalla(), config.getAltoPantalla());
                } else if (estadoJuego == GameState.MODAL_COFRE) {
                    renderSystem.getHudRenderer().renderCofreModal(g2, config.getAnchoPantalla(), config.getAltoPantalla(), gameEngine.getAcertijoActual());
                } else if (estadoJuego == GameState.MODAL_RESPUESTA_CORRECTA) {
                    renderSystem.renderRespuestaCorrecta(g2, config.getAnchoPantalla(), config.getAltoPantalla());
                } else if (estadoJuego == GameState.MODAL_RESPUESTA_INCORRECTA) {
                    int intentosRestantes = gameEngine.getAcertijoActual() != null ? 
                        gameEngine.getAcertijoActual().getIntentosRestantes() : 0;
                    renderSystem.renderRespuestaIncorrecta(g2, config.getAnchoPantalla(), config.getAltoPantalla(), intentosRestantes);
                }
                break;
        }

        g2.dispose();
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    private void manejarInputTexto(java.awt.event.KeyEvent e) {
        if (estadoJuego != GameState.UNIRSE_SERVIDOR) {
            return;
        }

        char c = e.getKeyChar();
        
        if (c == '\b') {
            if (inputServerID.length() > 0) {
                inputServerID = inputServerID.substring(0, inputServerID.length() - 1);
            }
        } else if (Character.isLetterOrDigit(c) || c == '-' || c == '.') {
            if (inputServerID.length() < 20) {
                inputServerID += Character.toUpperCase(c);
            }
        }
    }

    private void resetearInputs() {
        inputServerID = "";
        serverID = "";
        mostrarMensajeCopiado = false;
    }

    private String generarServerID() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder id = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < 8; i++) {
            if (i == 4) {
                id.append('-');
            }
            id.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        
        return id.toString();
    }

    private void copiarAlPortapapeles(String texto) {
        try {
            java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(texto);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        } catch (Exception ex) {
            System.err.println("Error al copiar al portapapeles: " + ex.getMessage());
        }
    }
}
