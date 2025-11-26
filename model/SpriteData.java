package model;

import java.awt.image.BufferedImage;

public class SpriteData {
    private BufferedImage arriba1, arriba2, abajo1, abajo2;
    private BufferedImage izquierda1, izquierda2, derecha1, derecha2;
    private String direccion;
    private int contadorSprites;
    private int numeroSprite;
    private int cambioSprite;

    public SpriteData() {
        this.direccion = "abajo";
        this.contadorSprites = 0;
        this.numeroSprite = 1;
        this.cambioSprite = 10;
    }

    public BufferedImage getArriba1() {
        return arriba1;
    }

    public void setArriba1(BufferedImage arriba1) {
        this.arriba1 = arriba1;
    }

    public BufferedImage getArriba2() {
        return arriba2;
    }

    public void setArriba2(BufferedImage arriba2) {
        this.arriba2 = arriba2;
    }

    public BufferedImage getAbajo1() {
        return abajo1;
    }

    public void setAbajo1(BufferedImage abajo1) {
        this.abajo1 = abajo1;
    }

    public BufferedImage getAbajo2() {
        return abajo2;
    }

    public void setAbajo2(BufferedImage abajo2) {
        this.abajo2 = abajo2;
    }

    public BufferedImage getIzquierda1() {
        return izquierda1;
    }

    public void setIzquierda1(BufferedImage izquierda1) {
        this.izquierda1 = izquierda1;
    }

    public BufferedImage getIzquierda2() {
        return izquierda2;
    }

    public void setIzquierda2(BufferedImage izquierda2) {
        this.izquierda2 = izquierda2;
    }

    public BufferedImage getDerecha1() {
        return derecha1;
    }

    public void setDerecha1(BufferedImage derecha1) {
        this.derecha1 = derecha1;
    }

    public BufferedImage getDerecha2() {
        return derecha2;
    }

    public void setDerecha2(BufferedImage derecha2) {
        this.derecha2 = derecha2;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getContadorSprites() {
        return contadorSprites;
    }

    public void setContadorSprites(int contadorSprites) {
        this.contadorSprites = contadorSprites;
    }

    public int getNumeroSprite() {
        return numeroSprite;
    }

    public void setNumeroSprite(int numeroSprite) {
        this.numeroSprite = numeroSprite;
    }

    public int getCambioSprite() {
        return cambioSprite;
    }

    public void setCambioSprite(int cambioSprite) {
        this.cambioSprite = cambioSprite;
    }

    public BufferedImage getSpriteActual() {
        switch (direccion) {
            case "arriba":
                return numeroSprite == 1 ? arriba1 : arriba2;
            case "abajo":
                return numeroSprite == 1 ? abajo1 : abajo2;
            case "izquierda":
                return numeroSprite == 1 ? izquierda1 : izquierda2;
            case "derecha":
                return numeroSprite == 1 ? derecha1 : derecha2;
            default:
                return abajo1;
        }
    }
}
