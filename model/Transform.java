package model;

public class Transform {
    private int x;
    private int y;
    private int velocidad;

    public Transform(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public void mover(int deltaX, int deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }
}
