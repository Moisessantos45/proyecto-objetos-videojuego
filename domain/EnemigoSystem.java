package domain;

import model.EnemigoModel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import infrastructure.ResourceLoader;

public class EnemigoSystem implements IUpdateable {
    private List<EnemigoModel> enemigos;
    private Random random;
    private int contadorUpdate;
    private static final String[] TIPOS_ENEMIGOS = {"goblin", "duende/masulino", "duende/femenino"};
    private int tamanioTile;
    
    public EnemigoSystem(int tamanioTile) {
        this.enemigos = new ArrayList<>();
        this.random = new Random();
        this.contadorUpdate = 0;
        this.tamanioTile = tamanioTile;
    }
    
    public void generarEnemigosIniciales(int jugadorX, int jugadorY, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            int x, y;
            do {
                x = jugadorX + (random.nextInt(2000) - 1000);
                y = jugadorY + (random.nextInt(2000) - 1000);
            } while (Math.abs(x - jugadorX) < 200 || Math.abs(y - jugadorY) < 200);
            
            agregarEnemigoAleatorio(x, y);
        }
    }
    
    public void agregarEnemigoAleatorio(int x, int y) {
        String tipo = TIPOS_ENEMIGOS[random.nextInt(TIPOS_ENEMIGOS.length)];
        agregarEnemigo(x, y, tipo);
    }
    
    public void agregarEnemigo(int x, int y, String tipo) {
        int velocidad = tipo.equals("goblin") ? 2 : 1;
        double vida = tipo.equals("goblin") ? 50.0 : 30.0;
        double danio = tipo.equals("goblin") ? 10.0 : 5.0; // Asignar daño por tipo
        
        EnemigoModel enemigo = new EnemigoModel(x, y, velocidad, tipo);
        enemigo.setVida(vida);
        enemigo.setDamage(danio); // Establecer el daño en el modelo

        // Configurar radios de IA
        enemigo.setDetectionRadius(5 * this.tamanioTile); // Radio de 5 tiles
        enemigo.setTerritoryRadius(10 * this.tamanioTile); // Radio de 10 tiles
        
        cargarAnimaciones(enemigo, tipo);
        enemigos.add(enemigo);
    }
    
    private void cargarAnimaciones(EnemigoModel enemigo, String tipo) {
        ResourceLoader loader = ResourceLoader.getInstance();
        
        String[] estados = {"Front - Idle", "Back - Idle", "Left - Idle", "Right - Idle",
                           "Front - Walking", "Back - Walking", "Left - Walking", "Right - Walking",
                           "Front - Running", "Back - Running", "Left - Running", "Right - Running",
                           "Front - Attacking", "Back - Attacking", "Left - Attacking", "Right - Attacking",
                           "Front - Hurt", "Back - Hurt", "Left - Hurt", "Right - Hurt",
                           "Dying"};
        
        for (String estado : estados) {
            BufferedImage[] frames = loader.cargarAnimacionEnemigo(tipo, estado);
            if (frames.length > 0) {
                enemigo.agregarAnimacion(estado, frames);
            }
        }
        
        mapearAnimacionesAlternativas(enemigo);
    }
    
    private void mapearAnimacionesAlternativas(EnemigoModel enemigo) {
        if (!enemigo.getAnimaciones().containsKey("Front - Walking") && 
            enemigo.getAnimaciones().containsKey("Front - Running")) {
            enemigo.agregarAnimacion("Front - Walking", 
                enemigo.getAnimaciones().get("Front - Running"));
        }
        if (!enemigo.getAnimaciones().containsKey("Back - Walking") && 
            enemigo.getAnimaciones().containsKey("Back - Running")) {
            enemigo.agregarAnimacion("Back - Walking", 
                enemigo.getAnimaciones().get("Back - Running"));
        }
        if (!enemigo.getAnimaciones().containsKey("Left - Walking") && 
            enemigo.getAnimaciones().containsKey("Left - Running")) {
            enemigo.agregarAnimacion("Left - Walking", 
                enemigo.getAnimaciones().get("Left - Running"));
        }
        if (!enemigo.getAnimaciones().containsKey("Right - Walking") && 
            enemigo.getAnimaciones().containsKey("Right - Running")) {
            enemigo.agregarAnimacion("Right - Walking", 
                enemigo.getAnimaciones().get("Right - Running"));
        }
        
        if (!enemigo.getAnimaciones().containsKey("Front - Idle") && 
            enemigo.getAnimaciones().containsKey("Front - Running")) {
            BufferedImage[] running = enemigo.getAnimaciones().get("Front - Running");
            if (running.length > 0) {
                enemigo.agregarAnimacion("Front - Idle", new BufferedImage[]{running[0]});
            }
        }
        if (!enemigo.getAnimaciones().containsKey("Back - Idle") && 
            enemigo.getAnimaciones().get("Back - Idle") == null &&
            enemigo.getAnimaciones().containsKey("Back - Running")) {
            BufferedImage[] running = enemigo.getAnimaciones().get("Back - Running");
            if (running.length > 0) {
                BufferedImage[] idle = enemigo.getAnimaciones().get("Back - Idle");
                if (idle == null || idle.length == 0) {
                    enemigo.agregarAnimacion("Back - Idle", new BufferedImage[]{running[0]});
                }
            }
        }
        if (!enemigo.getAnimaciones().containsKey("Left - Idle") && 
            enemigo.getAnimaciones().containsKey("Left - Running")) {
            BufferedImage[] running = enemigo.getAnimaciones().get("Left - Running");
            if (running.length > 0) {
                enemigo.agregarAnimacion("Left - Idle", new BufferedImage[]{running[0]});
            }
        }
        if (!enemigo.getAnimaciones().containsKey("Right - Idle") && 
            enemigo.getAnimaciones().get("Right - Idle") == null &&
            enemigo.getAnimaciones().containsKey("Right - Running")) {
            BufferedImage[] running = enemigo.getAnimaciones().get("Right - Running");
            if (running.length > 0) {
                BufferedImage[] idle = enemigo.getAnimaciones().get("Right - Idle");
                if (idle == null || idle.length == 0) {
                    enemigo.agregarAnimacion("Right - Idle", new BufferedImage[]{running[0]});
                }
            }
        }
    }
    
    @Override
    public void update() {
        // Este método está aquí para cumplir con la interfaz IUpdateable.
        // La lógica principal está en update(JugadorSystem).
    }

    public void update(JugadorSystem jugadorSystem) {
        contadorUpdate++;
        for (EnemigoModel enemigo : new ArrayList<>(enemigos)) {
            actualizarEnemigo(enemigo, jugadorSystem);
        }
    }

    private void actualizarEnemigo(EnemigoModel enemigo, JugadorSystem jugadorSystem) {
        // 1. Calcular distancia al jugador
        int jugadorX = jugadorSystem.getMundoX();
        int jugadorY = jugadorSystem.getMundoY();
        int enemigoX = enemigo.getTransform().getX();
        int enemigoY = enemigo.getTransform().getY();

        double distanciaAlJugador = Math.sqrt(Math.pow(jugadorX - enemigoX, 2) + Math.pow(jugadorY - enemigoY, 2));

        // 2. Decidir estado (Patrulla o Persecución) con histéresis
        if (distanciaAlJugador <= enemigo.getDetectionRadius()) {
            enemigo.setChasing(true);
        } else if (distanciaAlJugador > enemigo.getDetectionRadius() * 1.1) { // Margen para evitar parpadeo
            enemigo.setChasing(false);
        }

        // 3. Actuar según el estado
        if (enemigo.isChasing()) {
            perseguir(enemigo, jugadorSystem);
        } else {
            patrullar(enemigo, jugadorSystem);
        }

        // 4. Actualizar animación
        actualizarAnimacion(enemigo);
    }

    private void patrullar(EnemigoModel enemigo, JugadorSystem jugadorSystem) {
        // Cambia de dirección aleatoriamente de vez en cuando
        if (contadorUpdate % 120 == 0) {
            int dir = random.nextInt(5);
            String[] direcciones = {"arriba", "abajo", "izquierda", "derecha", "idle"};
            enemigo.getSpriteData().setDireccion(direcciones[dir]);
        }
        
        moverEnemigo(enemigo, enemigo.getSpriteData().getDireccion(), jugadorSystem);
    }

    private void perseguir(EnemigoModel enemigo, JugadorSystem jugadorSystem) {
        int enemigoX = enemigo.getTransform().getX();
        int enemigoY = enemigo.getTransform().getY();
        int jugadorX = jugadorSystem.getMundoX();
        int jugadorY = jugadorSystem.getMundoY();
        
        int dx = jugadorX - enemigoX;
        int dy = jugadorY - enemigoY;

        // Zona muerta para que el enemigo se pare si está muy cerca
        if (Math.abs(dx) < 5 && Math.abs(dy) < 5) {
            enemigo.getSpriteData().setDireccion("idle");
            moverEnemigo(enemigo, "idle", jugadorSystem);
            return;
        }

        String direccion;
        if (Math.abs(dx) > Math.abs(dy)) {
            direccion = dx > 0 ? "derecha" : "izquierda";
        } else {
            direccion = dy > 0 ? "abajo" : "arriba";
        }
        
        enemigo.getSpriteData().setDireccion(direccion);
        moverEnemigo(enemigo, direccion, jugadorSystem);
    }

    private void moverEnemigo(EnemigoModel enemigo, String direccion, JugadorSystem jugadorSystem) {
        int velocidad = enemigo.getTransform().getVelocidad();
        int dx = 0, dy = 0;

        switch(direccion) {
            case "arriba": dy = -velocidad; break;
            case "abajo":  dy = velocidad; break;
            case "izquierda": dx = -velocidad; break;
            case "derecha": dx = velocidad; break;
            default: break; // idle
        }

        if (dx != 0 || dy != 0) {
            int proximaX = enemigo.getTransform().getX() + dx;
            int proximaY = enemigo.getTransform().getY() + dy;

            // --- Verificación de colisión con el jugador ---
            int margen = 8;
            int anchoCuerpo = tamanioTile - 2 * margen;
            int altoCuerpo = tamanioTile - 2 * margen;
            
            if (ColisionSystem.verificarColisionEntidades(
                    proximaX + margen, proximaY + margen, anchoCuerpo, altoCuerpo,
                    jugadorSystem.getMundoX() + margen, jugadorSystem.getMundoY() + margen, anchoCuerpo, altoCuerpo)) {
                
                enemigo.getSpriteData().setDireccion("idle"); // Choca, así que se para
                determinarEstadoVisual(enemigo, false);
                return; // No moverse
            }

            // --- Verificación de territorio ---
            double distanciaAlSpawn = Math.sqrt(Math.pow(proximaX - enemigo.getSpawnX(), 2) + Math.pow(proximaY - enemigo.getSpawnY(), 2));
            if (distanciaAlSpawn > enemigo.getTerritoryRadius()) {
                enemigo.getSpriteData().setDireccion("idle"); // En el límite, se para
                determinarEstadoVisual(enemigo, false);
                return; // No moverse
            }

            // Si no hay colisiones, mover
            enemigo.getTransform().mover(dx, dy);
        }

        boolean enMovimiento = !direccion.equals("idle");
        determinarEstadoVisual(enemigo, enMovimiento);
    }

    private void determinarEstadoVisual(EnemigoModel enemigo, boolean enMovimiento) {
        String accion = enMovimiento ? "Walking" : "Idle";
        String direccionSprite;
        
        switch(enemigo.getSpriteData().getDireccion()) {
            case "arriba": direccionSprite = "Back"; break;
            case "abajo": direccionSprite = "Front"; break;
            case "izquierda": direccionSprite = "Left"; break;
            case "derecha": direccionSprite = "Right"; break;
            default: direccionSprite = "Front"; // Idle
        }

        String nuevoEstado = direccionSprite + " - " + accion;
        
        if (!nuevoEstado.equals(enemigo.getEstadoActual()) && enemigo.getAnimaciones().containsKey(nuevoEstado)) {
            enemigo.setEstadoActual(nuevoEstado);
            enemigo.setFrameActual(0);
        }
    }

    private void actualizarAnimacion(EnemigoModel enemigo) {
        if (contadorUpdate % 8 == 0) {
            BufferedImage[] frames = enemigo.getAnimaciones().get(enemigo.getEstadoActual());
            if (frames != null && frames.length > 0) {
                int frame = enemigo.getFrameActual();
                frame++;
                if (frame >= frames.length) {
                    frame = 0;
                }
                enemigo.setFrameActual(frame);
            }
        }
    }
    
    public List<EnemigoModel> getEnemigos() {
        return enemigos;
    }
    
    public void eliminarEnemigo(EnemigoModel enemigo) {
        enemigos.remove(enemigo);
    }
    
    public int getCantidadEnemigos() {
        return enemigos.size();
    }
}
