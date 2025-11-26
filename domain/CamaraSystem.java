package domain;

public class CamaraSystem {
    private int camaraX;
    private int camaraY;
    private int anchoPantalla;
    private int altoPantalla;

    public CamaraSystem(int anchoPantalla, int altoPantalla) {
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        this.camaraX = 0;
        this.camaraY = 0;
    }

    public void seguirEntidad(int mundoX, int mundoY) {
        camaraX = mundoX - anchoPantalla / 2;
        camaraY = mundoY - altoPantalla / 2;
    }

    public void moverCamara(int deltaX, int deltaY) {
        camaraX += deltaX;
        camaraY += deltaY;
    }

    public int getCamaraX() {
        return camaraX;
    }

    public int getCamaraY() {
        return camaraY;
    }

    public void setCamaraX(int camaraX) {
        this.camaraX = camaraX;
    }

    public void setCamaraY(int camaraY) {
        this.camaraY = camaraY;
    }

    public int convertirMundoAPantallaX(int mundoX) {
        return mundoX - camaraX;
    }

    public int convertirMundoAPantallaY(int mundoY) {
        return mundoY - camaraY;
    }
}
