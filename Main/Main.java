package Main;

import javax.swing.JFrame;

import domain.CamaraSystem;
import domain.ColisionSystem;
import domain.EnemigoSystem;
import domain.GameEngine;
import domain.JugadorSystem;
import domain.MapaInfinitoAdapter;
import infrastructure.ConfigManager;
import infrastructure.InputService;
import model.GameConfig;

public class Main {

    public static void main(String[] args) {
        // Forzar uso de IPv4 para evitar problemas de conexión entre Windows/Linux
        System.setProperty("java.net.preferIPv4Stack", "true");

        // 1. INFRASTRUCTURE - Servicios y configuración
        ConfigManager configManager = ConfigManager.getInstance();
        GameConfig config = configManager.getGameConfig();
        InputService inputService = new InputService();

        // 2. Crear componentes del mundo usando el adaptador
        MapaInfinitoAdapter mapaAdapter = new MapaInfinitoAdapter(config.getTamanioTile(), config.getAnchoPantalla(), config.getAltoPantalla());

        // 3. DOMAIN - Sistemas de lógica
        EnemigoSystem enemigoSystem = new EnemigoSystem(config.getTamanioTile());
        ColisionSystem colisionSystem = new ColisionSystem(mapaAdapter, enemigoSystem);
        
        JugadorSystem jugadorSystem = new JugadorSystem(
            config.getAnchoPantalla(),
            config.getAltoPantalla(),
            config.getTamanioTile(),
            inputService,
            colisionSystem
        );

        CamaraSystem camaraSystem = new CamaraSystem(
            config.getAnchoPantalla(),
            config.getAltoPantalla()
        );

        // 4. Motor del juego
        GameEngine gameEngine = new GameEngine(
            config,
            jugadorSystem,
            camaraSystem,
            mapaAdapter,
            enemigoSystem,
            inputService
        );

        // 5. PRESENTATION - Vista
        GamePanel gamePanel = new GamePanel(gameEngine, inputService);

        // 6. Crear ventana
        JFrame ventana = new JFrame("VideoJuego - Arquitectura por Capas");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        ventana.add(gamePanel);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        gamePanel.requestFocusInWindow(); // Asegurar que el panel tenga el foco

        // 7. Iniciar el juego
        gamePanel.iniciarJuego();

        System.out.println("=== Juego iniciado con nueva arquitectura ===");
        System.out.println("MODEL: Datos puros (Transform, SpriteData, EntidadModel)");
        System.out.println("DOMAIN: Lógica (MovimientoSystem, ColisionSystem, AnimacionSystem)");
        System.out.println("INFRASTRUCTURE: Servicios (InputService, ResourceLoader)");
        System.out.println("PRESENTATION: Vista (Renderers, GamePanel)");
    }
}
