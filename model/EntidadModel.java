package model;

public class EntidadModel {
    private Transform transform;
    private SpriteData spriteData;
    private double vida;
    private boolean esJugador;

    public EntidadModel(int x, int y, int velocidad) {
        this.transform = new Transform(x, y, velocidad);
        this.spriteData = new SpriteData();
        this.vida = 100.0;
        this.esJugador = false;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public SpriteData getSpriteData() {
        return spriteData;
    }

    public void setSpriteData(SpriteData spriteData) {
        this.spriteData = spriteData;
    }

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
        this.vida = vida;
    }

    public boolean isEsJugador() {
        return esJugador;
    }

    public void setEsJugador(boolean esJugador) {
        this.esJugador = esJugador;
    }
}
