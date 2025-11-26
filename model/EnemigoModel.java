package model;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class EnemigoModel extends EntidadModel {
    private Map<String, BufferedImage[]> animaciones;
    private String estadoActual;
    private int frameActual;
    private String tipoEnemigo;

    // Nuevos campos para IA de movimiento
    private int detectionRadius;
    private int territoryRadius;
    private int spawnX;
    private int spawnY;
    private boolean isChasing;
    
    public EnemigoModel(int x, int y, int velocidad, String tipoEnemigo) {
        super(x, y, velocidad);
        this.tipoEnemigo = tipoEnemigo;
        this.animaciones = new HashMap<>();
        this.estadoActual = "Front - Idle";
        this.frameActual = 0;
        setEsJugador(false);

        // Inicializar nuevos campos
        this.spawnX = x;
        this.spawnY = y;
        this.isChasing = false;
    }
    
    public Map<String, BufferedImage[]> getAnimaciones() {
        return animaciones;
    }
    
    public void agregarAnimacion(String nombre, BufferedImage[] frames) {
        animaciones.put(nombre, frames);
    }
    
    public String getEstadoActual() {
        return estadoActual;
    }
    
    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }
    
    public int getFrameActual() {
        return frameActual;
    }
    
    public void setFrameActual(int frameActual) {
        this.frameActual = frameActual;
    }
    
    public String getTipoEnemigo() {
        return tipoEnemigo;
    }
    
    public BufferedImage getFrameActualImagen() {
        BufferedImage[] frames = animaciones.get(estadoActual);
        if (frames != null && frameActual < frames.length) {
            return frames[frameActual];
        }
        return null;
    }

    // Getters y Setters para nuevos campos

    public int getDetectionRadius() {
        return detectionRadius;
    }

    public void setDetectionRadius(int detectionRadius) {
        this.detectionRadius = detectionRadius;
    }

    public int getTerritoryRadius() {
        return territoryRadius;
    }

    public void setTerritoryRadius(int territoryRadius) {
        this.territoryRadius = territoryRadius;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public boolean isChasing() {
        return isChasing;
    }

    public void setChasing(boolean isChasing) {
        this.isChasing = isChasing;
    }

    private double damage;

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}