package domain;

import java.awt.image.BufferedImage;
import java.util.Random;
import model.Chunk;
import model.ConsumableItemModel;
import infrastructure.ResourceLoader;

public class GeneradorMundo {

    private long seed;
    private Random random;
    private ManejadorMapaInfinito manejadorMapaInfinito;
    private ResourceLoader resourceLoader;

    public static final int TILE_AGUA = 0;
    public static final int TILE_ARBOL = 1;
    public static final int TILE_ARENA = 2;
    public static final int TILE_MURO = 3;
    public static final int TILE_PASTO = 4;
    public static final int TILE_SUELO = 5;
    public static final int TILE_PIEDRA = 6;
    public static final int TILE_NUBE = 7;
    public static final int TILE_VOLCAN = 8;
    public static final int TILE_COFRE = 9;
    public static final int TILE_COFRE_CERRADO = 10;

    private static final int MAX_POTIONS_1 = 12;
    private static final int MAX_POTIONS_2 = 12;
    private static final int MAX_POISONS = 12;

    public GeneradorMundo(long seed, ManejadorMapaInfinito manejadorMapaInfinito) {
        this.seed = seed;
        this.random = new Random(seed);
        this.manejadorMapaInfinito = manejadorMapaInfinito;
        this.resourceLoader = ResourceLoader.getInstance();
    }

    public long getSeed() {
        return seed;
    }

    public void generarChunk(Chunk chunk) {
        if (chunk.isGenerado()) {
            return;
        }

        int chunkX = chunk.getChunkX();
        int chunkY = chunk.getChunkY();

        for (int localY = 0; localY < Chunk.CHUNK_SIZE; localY++) {
            for (int localX = 0; localX < Chunk.CHUNK_SIZE; localX++) {
                int worldX = chunkX * Chunk.CHUNK_SIZE + localX;
                int worldY = chunkY * Chunk.CHUNK_SIZE + localY;

                int tileType = generarTile(worldX, worldY);

                if (tileType == TILE_PASTO) {
                    if (random.nextDouble() < 0.01) { // 1% de probabilidad de cofre
                        tileType = TILE_COFRE;
                    }
                }

                chunk.setTile(localX, localY, tileType);

                // Probabilidad de generar un item consumible
                if (tileType == TILE_PASTO || tileType == TILE_SUELO) {
                    if (random.nextDouble() < 0.02) { // 2% de probabilidad
                        generarItemConsumible(worldX, worldY);
                    }
                }
            }
        }

        chunk.setGenerado(true);
    }

    private int generarTile(int worldX, int worldY) {
        long hash = seed;
        hash = hash * 31 + worldX;
        hash = hash * 31 + worldY;

        Random localRandom = new Random(hash);
        double noise = generarNoise(worldX, worldY);

        if (noise < 0.2) {
            return TILE_AGUA;
        } else if (noise < 0.35) {
            return TILE_ARENA;
        } else if (noise < 0.5) {
            if (localRandom.nextDouble() < 0.5) {
                return TILE_PASTO;
            }
            return TILE_SUELO;
        } else if (noise < 0.65) {
            if (localRandom.nextDouble() < 0.5) {
                return TILE_ARBOL;
            }
            return TILE_PASTO;
        } else if (noise < 0.75) {
            if (localRandom.nextDouble() < 0.4) {
                return TILE_PIEDRA;
            }
            return TILE_PASTO;
        } else if (noise < 0.85) {
            if (localRandom.nextDouble() < 0.6) {
                return TILE_MURO;
            }
            return TILE_PASTO;
        } else if (noise < 0.92) {
            if (localRandom.nextDouble() < 0.3) {
                return TILE_NUBE;
            }
            return TILE_PASTO;
        } else {
            if (localRandom.nextDouble() < 0.5) {
                return TILE_VOLCAN;
            }
            return TILE_PIEDRA;
        }
    }

    private double generarNoise(int x, int y) {
        double escala = 0.1;

        double nx = x * escala;
        double ny = y * escala;

        double valor = 0.0;
        double amplitud = 1.0;
        double frecuencia = 1.0;
        double maxValor = 0.0;

        for (int i = 0; i < 4; i++) {
            valor += noise2D(nx * frecuencia, ny * frecuencia) * amplitud;
            maxValor += amplitud;
            amplitud *= 0.5;
            frecuencia *= 2.0;
        }

        return (valor / maxValor + 1.0) / 2.0;
    }

    private double noise2D(double x, double y) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);

        double xf = x - xi;
        double yf = y - yi;

        double n00 = hash2D(xi, yi);
        double n10 = hash2D(xi + 1, yi);
        double n01 = hash2D(xi, yi + 1);
        double n11 = hash2D(xi + 1, yi + 1);

        double u = suavizar(xf);
        double v = suavizar(yf);

        double x1 = interpolar(n00, n10, u);
        double x2 = interpolar(n01, n11, u);

        return interpolar(x1, x2, v);
    }

    private double hash2D(int x, int y) {
        long n = seed;
        n = n * 31 + x;
        n = n * 31 + y;
        n = ((n << 13) ^ n);

        return (
            1.0 -
            ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) /
            1073741824.0
        );
    }

    private double interpolar(double a, double b, double t) {
        return a + t * (b - a);
    }

    private double suavizar(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    public void setSeed(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    private void generarItemConsumible(int worldX, int worldY) {
        int numPotions1 = 0;
        int numPotions2 = 0;
        int numPoisons = 0;

        for (ConsumableItemModel item : manejadorMapaInfinito.getItemsConsumibles()) {
            if (!item.isPickedUp()) {
                if (item.getType() == ConsumableItemModel.ItemType.POTION) {
                    if (item.getVariant() == 1) {
                        numPotions1++;
                    } else if (item.getVariant() == 2) {
                        numPotions2++;
                    }
                } else if (item.getType() == ConsumableItemModel.ItemType.POISON) {
                    numPoisons++;
                }
            }
        }

        double rand = random.nextDouble();

        if (rand < 0.4 && numPotions1 < MAX_POTIONS_1) {
            crearItem(worldX, worldY, ConsumableItemModel.ItemType.POTION, 1);
        } else if (rand < 0.8 && numPotions2 < MAX_POTIONS_2) {
            crearItem(worldX, worldY, ConsumableItemModel.ItemType.POTION, 2);
        } else if (numPoisons < MAX_POISONS) {
            crearItem(worldX, worldY, ConsumableItemModel.ItemType.POISON, 0);
        }
    }

    private void crearItem(int worldX, int worldY, ConsumableItemModel.ItemType type, int variant) {
        BufferedImage itemSprite;
        double effectValue;
        String spriteName;

        if (type == ConsumableItemModel.ItemType.POTION) {
            if (variant == 1) {
                spriteName = "pocion_1";
                effectValue = 25;
            } else {
                spriteName = "pocion_2";
                effectValue = 50;
            }
        } else { // POISON
            spriteName = "veneno";
            effectValue = 10;
        }

        itemSprite = resourceLoader.cargarItemSprite(spriteName);

        if (itemSprite != null) {
            int itemX = worldX * manejadorMapaInfinito.getTamanioTile();
            int itemY = worldY * manejadorMapaInfinito.getTamanioTile();
            ConsumableItemModel item = new ConsumableItemModel(itemX, itemY, type, effectValue, 1, itemSprite, variant);
            manejadorMapaInfinito.agregarItem(item);
        }
    }
    
    public void cambiarSeed(long nuevoSeed) {
        setSeed(nuevoSeed);
        System.out.println("Generador de mundo actualizado con nuevo seed: " + nuevoSeed);
    }
}