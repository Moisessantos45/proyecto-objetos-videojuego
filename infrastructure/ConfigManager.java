package infrastructure;

import model.GameConfig;

public class ConfigManager {
    private static ConfigManager instance;
    private GameConfig gameConfig;

    private ConfigManager() {
        this.gameConfig = new GameConfig();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public int getTamanioTile() {
        return gameConfig.getTamanioTile();
    }

    public int getAnchoPantalla() {
        return gameConfig.getAnchoPantalla();
    }

    public int getAltoPantalla() {
        return gameConfig.getAltoPantalla();
    }

    public int getFPS() {
        return gameConfig.getFps();
    }
}
