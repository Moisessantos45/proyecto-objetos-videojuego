package domain;

import model.EntidadModel;
import model.ConsumableItemModel;
import infrastructure.InputService;
import infrastructure.ResourceLoader;
import model.SpriteData;
import java.awt.image.BufferedImage;
import java.awt.Event;

public class JugadorSystem implements IUpdateable {
    private EntidadModel jugador;
    private MovimientoSystem movimientoSystem;
    private AnimacionSystem animacionSystem;
    private InputService inputService;
    private long ultimoTiempoDanio;
    private static final long COOLDOWN_DANIO = 1000; // 1 segundo en ms
    private int pocionesEnArsenal;
    private int acertijosResueltos;

    public JugadorSystem(int pantallaAncho, int pantAlto, int tamanioTile, 
                         InputService inputService, ColisionSystem colisionSystem) {
        this.inputService = inputService;
        
        int posX = (pantallaAncho / 2) - (tamanioTile / 2);
        int posY = (pantAlto / 2) - (tamanioTile / 2);
        
        this.jugador = new EntidadModel(posX, posY, 4);
        this.jugador.setEsJugador(true);
        this.jugador.setVida(100); // Inicializar vida del jugador
        this.ultimoTiempoDanio = 0;
        this.pocionesEnArsenal = 0; // Inicializar pociones en arsenal
        this.acertijosResueltos = 0;
        
        cargarSprites();
        
        this.movimientoSystem = new MovimientoSystem(jugador, inputService, colisionSystem);
        this.animacionSystem = new AnimacionSystem(jugador.getSpriteData());
    }

    private void cargarSprites() {
        ResourceLoader loader = ResourceLoader.getInstance();
        BufferedImage[] sprites = loader.cargarSpritesJugador();
        
        SpriteData spriteData = jugador.getSpriteData();
        spriteData.setArriba1(sprites[0]);
        spriteData.setArriba2(sprites[1]);
        spriteData.setAbajo1(sprites[2]);
        spriteData.setAbajo2(sprites[3]);
        spriteData.setIzquierda1(sprites[4]);
        spriteData.setIzquierda2(sprites[5]);
        spriteData.setDerecha1(sprites[6]);
        spriteData.setDerecha2(sprites[7]);
    }

    @Override
    public void update() {
        if (inputService.hayMovimiento()) {
            movimientoSystem.update();
            animacionSystem.update();
        }

        if (inputService.isTeclaUsarPocion()) {
            usarPocion();
            inputService.setTeclaUsarPocion(false); // Resetear el estado de la tecla
        }
    }

    public EntidadModel getJugador() {
        return jugador;
    }

    public MovimientoSystem getMovimientoSystem() {
        return movimientoSystem;
    }

    public int getMundoX() {
        return movimientoSystem.getMundoX();
    }

    public int getMundoY() {
        return movimientoSystem.getMundoY();
    }

    public void recibirDanio(double danio) {
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoTiempoDanio > COOLDOWN_DANIO) {
            double vidaActual = this.jugador.getVida();
            this.jugador.setVida(vidaActual - danio);
            this.ultimoTiempoDanio = tiempoActual;
            System.out.println("¡Jugador recibe " + danio + " de daño! Vida restante: " + this.jugador.getVida());
        }
    }

    public void recogerItem(ConsumableItemModel item) {
        if (item.getType() == ConsumableItemModel.ItemType.POTION) {
            this.pocionesEnArsenal += item.getQuantity();
            System.out.println("DEBUG: Has recogido " + item.getQuantity() + " pocion(es). Tienes: " + pocionesEnArsenal);
        } else if (item.getType() == ConsumableItemModel.ItemType.POISON) {
            recibirDanio(item.getEffectValue());
            System.out.println("DEBUG: ¡Has tomado veneno! Daño: " + item.getEffectValue());
        }
    }

    public void usarPocion() {
        if (this.jugador.getVida() >= 100.0) {
            System.out.println("Tu vida ya está al máximo.");
            return;
        }

        if (pocionesEnArsenal > 0) {
            this.pocionesEnArsenal--;
            double vidaActual = this.jugador.getVida();
            this.jugador.setVida(Math.min(100.0, vidaActual + 40)); // Cura 40 de vida, con tope de 100
            System.out.println("DEBUG: Has usado una poción. Vida: " + this.jugador.getVida() + ". Pociones restantes: " + pocionesEnArsenal);
        } else {
            System.out.println("No tienes pociones en tu arsenal.");
            // TODO: Integrar con sistema de mensajes en pantalla (HUD)
        }
    }
    
    public int getPocionesEnArsenal() {
        return pocionesEnArsenal;
    }

    public int getAcertijosResueltos() {
        return acertijosResueltos;
    }

    public void incrementarAcertijosResueltos() {
        this.acertijosResueltos++;
    }

    public void setPocionesEnArsenal(int pociones) {
        this.pocionesEnArsenal = pociones;
    }

    public void setAcertijosResueltos(int acertijos) {
        this.acertijosResueltos = acertijos;
    }

    public void reiniciarCooldownDanio() {
        this.ultimoTiempoDanio = 0;
    }
}