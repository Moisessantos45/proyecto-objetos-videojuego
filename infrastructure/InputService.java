package infrastructure;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputService implements KeyListener {
    private boolean teclaArriba, teclaAbajo, teclaIzquierda, teclaDerecha;
    private boolean teclaEscape, teclaEnter, teclaEspacio;
    private boolean teclaUsarPocion;
    private boolean tecla1, tecla2, tecla3, tecla4;
    private boolean teclaA, teclaB, teclaC, teclaD;

    public InputService() {
        this.teclaArriba = false;
        this.teclaAbajo = false;
        this.teclaIzquierda = false;
        this.teclaDerecha = false;
        this.teclaEscape = false;
        this.teclaEnter = false;
        this.teclaEspacio = false;
        this.teclaUsarPocion = false;
        this.tecla1 = false;
        this.tecla2 = false;
        this.tecla3 = false;
        this.tecla4 = false;
        this.teclaA = false;
        this.teclaB = false;
        this.teclaC = false;
        this.teclaD = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                teclaArriba = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                teclaAbajo = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                teclaIzquierda = true;
                teclaA = true;
                break;
            case KeyEvent.VK_B:
                teclaB = true;
                break;
            case KeyEvent.VK_C:
                teclaC = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                teclaDerecha = true;
                teclaD = true;
                break;
            case KeyEvent.VK_ESCAPE:
                teclaEscape = true;
                break;
            case KeyEvent.VK_ENTER:
                teclaEnter = true;
                break;
            case KeyEvent.VK_SPACE:
                teclaEspacio = true;
                break;
            case KeyEvent.VK_Q:
                teclaUsarPocion = true;
                break;
            case KeyEvent.VK_1:
                tecla1 = true;
                break;
            case KeyEvent.VK_2:
                tecla2 = true;
                break;
            case KeyEvent.VK_3:
                tecla3 = true;
                break;
            case KeyEvent.VK_4:
                tecla4 = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                teclaArriba = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                teclaAbajo = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                teclaIzquierda = false;
                teclaA = false;
                break;
            case KeyEvent.VK_B:
                teclaB = false;
                break;
            case KeyEvent.VK_C:
                teclaC = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                teclaDerecha = false;
                teclaD = false;
                break;
            case KeyEvent.VK_ESCAPE:
                teclaEscape = false;
                break;
            case KeyEvent.VK_SPACE:
                teclaEspacio = false;
                break;
            case KeyEvent.VK_Q:
                teclaUsarPocion = false;
                break;
            case KeyEvent.VK_1:
                tecla1 = false;
                break;
            case KeyEvent.VK_2:
                tecla2 = false;
                break;
            case KeyEvent.VK_3:
                tecla3 = false;
                break;
            case KeyEvent.VK_4:
                tecla4 = false;
                break;
        }
    }

    public boolean isTeclaArriba() {
        return teclaArriba;
    }

    public boolean isTeclaAbajo() {
        return teclaAbajo;
    }

    public boolean isTeclaIzquierda() {
        return teclaIzquierda;
    }

    public boolean isTeclaDerecha() {
        return teclaDerecha;
    }

    public boolean isTeclaEscape() {
        return teclaEscape;
    }

    public boolean isTeclaEnter() {
        return teclaEnter;
    }

    public boolean isTeclaEspacio() {
        return teclaEspacio;
    }

    public boolean isTeclaUsarPocion() {
        return teclaUsarPocion;
    }

    public boolean isTecla1() {
        return tecla1;
    }

    public boolean isTecla2() {
        return tecla2;
    }

    public boolean isTecla3() {
        return tecla3;
    }

    public boolean isTecla4() {
        return tecla4;
    }

    public boolean isTeclaA() {
        return teclaA;
    }

    public boolean isTeclaB() {
        return teclaB;
    }

    public boolean isTeclaC() {
        return teclaC;
    }

    public boolean isTeclaD() {
        return teclaD;
    }

    public boolean hayMovimiento() {
        return teclaArriba || teclaAbajo || teclaIzquierda || teclaDerecha;
    }

    public void setTeclaEnter(boolean estado) {
        this.teclaEnter = estado;
    }

    public void setTeclaUsarPocion(boolean estado) {
        this.teclaUsarPocion = estado;
    }

    public void setTeclaEscape(boolean estado) {
        this.teclaEscape = estado;
    }

    public void setTecla1(boolean estado) {
        this.tecla1 = estado;
    }

    public void setTecla2(boolean estado) {
        this.tecla2 = estado;
    }

    public void setTecla3(boolean estado) {
        this.tecla3 = estado;
    }

    public void setTeclaC(boolean estado) {
        this.teclaC = estado;
    }
}
