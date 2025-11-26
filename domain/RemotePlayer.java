package domain;

import model.EntidadModel;
import model.SpriteData;
import infrastructure.ResourceLoader;
import java.awt.image.BufferedImage;

public class RemotePlayer {
    private EntidadModel entidad;
    private AnimacionSystem animacionSystem;
    private String id;

    public RemotePlayer(String id, int startX, int startY) {
        this.id = id;
        this.entidad = new EntidadModel(startX, startY, 4); // Velocidad por defecto
        this.entidad.setEsJugador(true); // Para usar sprites de jugador
        
        cargarSprites();
        this.animacionSystem = new AnimacionSystem(entidad.getSpriteData());
    }

    private void cargarSprites() {
        // Reutilizamos la carga de sprites del jugador
        ResourceLoader loader = ResourceLoader.getInstance();
        BufferedImage[] sprites = loader.cargarSpritesJugador();
        
        SpriteData spriteData = entidad.getSpriteData();
        spriteData.setArriba1(sprites[0]);
        spriteData.setArriba2(sprites[1]);
        spriteData.setAbajo1(sprites[2]);
        spriteData.setAbajo2(sprites[3]);
        spriteData.setIzquierda1(sprites[4]);
        spriteData.setIzquierda2(sprites[5]);
        spriteData.setDerecha1(sprites[6]);
        spriteData.setDerecha2(sprites[7]);
    }

    public void updatePosition(int x, int y, String direction) {
        entidad.setMundoX(x);
        entidad.setMundoY(y);
        entidad.setDireccion(direction);
        animacionSystem.update(); // Actualizar animación
    }

    public EntidadModel getEntidad() {
        return entidad;
    }
    
    public String getId() {
        return id;
    }
}
