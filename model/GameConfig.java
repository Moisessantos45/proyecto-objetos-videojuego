package model;

public class GameConfig {
    private final int tamanioOriginalTile;
    private final int escala;
    private final int tamanioTile;
    private final int maxRenPantalla;
    private final int maxColPantalla;
    private final int anchoPantalla;
    private final int altoPantalla;
    private final int fps;

    public GameConfig() {
        this.tamanioOriginalTile = 16;
        this.escala = 3;
        this.tamanioTile = tamanioOriginalTile * escala;
        this.maxRenPantalla = 15;
        this.maxColPantalla = 26;
        this.anchoPantalla = tamanioTile * maxColPantalla;
        this.altoPantalla = tamanioTile * maxRenPantalla;
        this.fps = 60;
    }

    public int getTamanioOriginalTile() {
        return tamanioOriginalTile;
    }

    public int getEscala() {
        return escala;
    }

    public int getTamanioTile() {
        return tamanioTile;
    }

    public int getMaxRenPantalla() {
        return maxRenPantalla;
    }

    public int getMaxColPantalla() {
        return maxColPantalla;
    }

    public int getAnchoPantalla() {
        return anchoPantalla;
    }

    public int getAltoPantalla() {
        return altoPantalla;
    }

    public int getFps() {
        return fps;
    }
}
