package domain;

import infrastructure.InputService;
import model.EntidadModel;
import model.Transform;

public class MovimientoSystem implements IUpdateable {
    private EntidadModel entidad;
    private InputService inputService;
    private ColisionSystem colisionSystem;
    private int mundoX;
    private int mundoY;
    private int ultimaColision = -1;

    public MovimientoSystem(EntidadModel entidad, InputService inputService, ColisionSystem colisionSystem) {
        this.entidad = entidad;
        this.inputService = inputService;
        this.colisionSystem = colisionSystem;
        this.mundoX = 5000;
        this.mundoY = 5000;
    }

    @Override
    public void update() {
        Transform transform = entidad.getTransform();
        int velocidad = transform.getVelocidad();
        boolean seMovio = false;
        ultimaColision = -1; // Reset collision state

        if (inputService.isTeclaArriba()) {
            int tileColision = colisionSystem.verificarColisionTiles(mundoX, mundoY, velocidad, "arriba");
            if (tileColision == -1) {
                mundoY -= velocidad;
                entidad.getSpriteData().setDireccion("arriba");
                seMovio = true;
            } else {
                ultimaColision = tileColision;
            }
        }
        if (inputService.isTeclaAbajo()) {
            int tileColision = colisionSystem.verificarColisionTiles(mundoX, mundoY, velocidad, "abajo");
            if (tileColision == -1) {
                mundoY += velocidad;
                entidad.getSpriteData().setDireccion("abajo");
                seMovio = true;
            } else {
                ultimaColision = tileColision;
            }
        }
        if (inputService.isTeclaIzquierda()) {
            int tileColision = colisionSystem.verificarColisionTiles(mundoX, mundoY, velocidad, "izquierda");
            if (tileColision == -1) {
                mundoX -= velocidad;
                entidad.getSpriteData().setDireccion("izquierda");
                seMovio = true;
            } else {
                ultimaColision = tileColision;
            }
        }
        if (inputService.isTeclaDerecha()) {
            int tileColision = colisionSystem.verificarColisionTiles(mundoX, mundoY, velocidad, "derecha");
            if (tileColision == -1) {
                mundoX += velocidad;
                entidad.getSpriteData().setDireccion("derecha");
                seMovio = true;
            } else {
                ultimaColision = tileColision;
            }
        }
    }

    public int getUltimaColision() {
        return ultimaColision;
    }

    public int getMundoX() {
        return mundoX;
    }

    public int getMundoY() {
        return mundoY;
    }

    public void setMundoX(int mundoX) {
        this.mundoX = mundoX;
    }

    public void setMundoY(int mundoY) {
        this.mundoY = mundoY;
    }
}
