package domain;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import model.Chunk;
import model.ConsumableItemModel;
import model.Tile;
import domain.GeneradorMundo;

public class ManejadorMapaInfinito {

    private int tamanioTile;
    private int anchoPantalla;
    private int altoPantalla;

    private Tile[] tiles;
    private HashMap<String, Chunk> chunksActivos;
    private GeneradorMundo generador;
    private List<ConsumableItemModel> itemsConsumibles;

    private static final int MAX_CHUNKS_EN_MEMORIA = 100;
    private static final int MARGEN_CHUNKS = 2;

    private int minChunkX, maxChunkX;
    private int minChunkY, maxChunkY;

    public ManejadorMapaInfinito(int tamanioTile, int anchoPantalla, int altoPantalla, long seed) {
        this.tamanioTile = tamanioTile;
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        this.chunksActivos = new HashMap<>();
        this.generador = new GeneradorMundo(seed, this);
        this.itemsConsumibles = new ArrayList<>();

        cargarTilesVisuales();

        System.out.println(
                "Sistema de mapa infinito inicializado con seed: " + seed);
    }

    private void cargarTilesVisuales() {
        tiles = new Tile[11];
        infrastructure.ResourceLoader loader = infrastructure.ResourceLoader.getInstance();

        try {
            tiles[0] = new Tile();
            tiles[0].setImage(loader.cargarImagen("tiles/agua.png"));
            tiles[0].setCollision(true);

            tiles[1] = new Tile();
            tiles[1].setImage(loader.cargarImagen("tiles/arbol.png"));
            tiles[1].setCollision(true);

            tiles[2] = new Tile();
            tiles[2].setImage(loader.cargarImagen("tiles/arena.png"));
            tiles[2].setCollision(false);

            tiles[3] = new Tile();
            tiles[3].setImage(loader.cargarImagen("tiles/muro.png"));
            tiles[3].setCollision(true);

            tiles[4] = new Tile();
            tiles[4].setImage(loader.cargarImagen("tiles/pasto.png"));
            tiles[4].setCollision(false);

            tiles[5] = new Tile();
            tiles[5].setImage(loader.cargarImagen("tiles/suelo.png"));
            tiles[5].setCollision(false);

            tiles[6] = new Tile();
            tiles[6].setImage(loader.cargarImagen("tiles/piedra.png"));
            tiles[6].setCollision(true);

            tiles[7] = new Tile();
            tiles[7].setImage(loader.cargarImagen("tiles/nube.png"));
            tiles[7].setCollision(false);

            tiles[8] = new Tile();
            tiles[8].setImage(loader.cargarImagen("tiles/volcan.png"));
            tiles[8].setCollision(true);

            tiles[9] = new Tile();
            tiles[9].setImage(loader.cargarImagen("tiles/cofre.png"));
            tiles[9].setCollision(true);

            tiles[10] = new Tile();
            tiles[10].setImage(loader.cargarImagen("tiles/cofre_cerrado.png"));
            tiles[10].setCollision(true);

            System.out.println("Tiles visuales cargados correctamente");
            System.out.println("Sistema de colisiones activado:");
        } catch (Exception e) {
            System.out.println("Error al cargar tiles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarChunksActivos(int jugadorX, int jugadorY) {
        int tileX = jugadorX / tamanioTile;
        int tileY = jugadorY / tamanioTile;

        int chunkJugadorX = Math.floorDiv(tileX, Chunk.CHUNK_SIZE);
        int chunkJugadorY = Math.floorDiv(tileY, Chunk.CHUNK_SIZE);

        int maxColPantalla = this.anchoPantalla / this.tamanioTile;
        int maxRenPantalla = this.altoPantalla / this.tamanioTile;

        int anchoVista = maxColPantalla / Chunk.CHUNK_SIZE + 1;
        int altoVista = maxRenPantalla / Chunk.CHUNK_SIZE + 1;

        minChunkX = chunkJugadorX - anchoVista / 2 - MARGEN_CHUNKS;
        maxChunkX = chunkJugadorX + anchoVista / 2 + MARGEN_CHUNKS;
        minChunkY = chunkJugadorY - altoVista / 2 - MARGEN_CHUNKS;
        maxChunkY = chunkJugadorY + altoVista / 2 + MARGEN_CHUNKS;

        for (int cy = minChunkY; cy <= maxChunkY; cy++) {
            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                cargarChunk(cx, cy);
            }
        }

        if (chunksActivos.size() > MAX_CHUNKS_EN_MEMORIA) {
            limpiarChunksLejanos();
        }
    }

    private void cargarChunk(int chunkX, int chunkY) {
        String key = Chunk.crearKey(chunkX, chunkY);

        if (!chunksActivos.containsKey(key)) {
            Chunk chunk = new Chunk(chunkX, chunkY);
            generador.generarChunk(chunk);
            chunksActivos.put(key, chunk);
        }
    }

    private void limpiarChunksLejanos() {
        Iterator<Map.Entry<String, Chunk>> iterator = chunksActivos
                .entrySet()
                .iterator();

        while (iterator.hasNext() &&
                chunksActivos.size() > MAX_CHUNKS_EN_MEMORIA * 0.8) {
            Map.Entry<String, Chunk> entry = iterator.next();
            Chunk chunk = entry.getValue();

            int cx = chunk.getChunkX();
            int cy = chunk.getChunkY();

            if (cx < minChunkX - MARGEN_CHUNKS ||
                    cx > maxChunkX + MARGEN_CHUNKS ||
                    cy < minChunkY - MARGEN_CHUNKS ||
                    cy > maxChunkY + MARGEN_CHUNKS) {
                iterator.remove();
            }
        }
    }

    public int getTileEnMundo(int tileX, int tileY) {
        int chunkX = Math.floorDiv(tileX, Chunk.CHUNK_SIZE);
        int chunkY = Math.floorDiv(tileY, Chunk.CHUNK_SIZE);

        String key = Chunk.crearKey(chunkX, chunkY);
        Chunk chunk = chunksActivos.get(key);

        if (chunk != null) {
            int localX = tileX - chunkX * Chunk.CHUNK_SIZE;
            int localY = tileY - chunkY * Chunk.CHUNK_SIZE;
            return chunk.getTile(localX, localY);
        }

        return 4;
    }

    public void setTileEnMundo(int tileX, int tileY, int tileType) {
        int chunkX = Math.floorDiv(tileX, Chunk.CHUNK_SIZE);
        int chunkY = Math.floorDiv(tileY, Chunk.CHUNK_SIZE);

        String key = Chunk.crearKey(chunkX, chunkY);
        Chunk chunk = chunksActivos.get(key);

        if (chunk == null) {
            cargarChunk(chunkX, chunkY);
            chunk = chunksActivos.get(key);
        }

        if (chunk != null) {
            int localX = tileX - chunkX * Chunk.CHUNK_SIZE;
            int localY = tileY - chunkY * Chunk.CHUNK_SIZE;
            chunk.setTile(localX, localY, tileType);
        }
    }

    public void draw(Graphics2D g2, int camaraX, int camaraY) {
        int primerTileX = Math.max(0, camaraX / tamanioTile);
        int primerTileY = Math.max(0, camaraY / tamanioTile);
        int ultimoTileX = (camaraX + anchoPantalla) / tamanioTile + 1;
        int ultimoTileY = (camaraY + altoPantalla) / tamanioTile + 1;

        for (int tileY = primerTileY; tileY <= ultimoTileY; tileY++) {
            for (int tileX = primerTileX; tileX <= ultimoTileX; tileX++) {
                int tileType = getTileEnMundo(tileX, tileY);

                int pantallaX = tileX * tamanioTile - camaraX;
                int pantallaY = tileY * tamanioTile - camaraY;

                if (tileType >= 0 &&
                        tileType < tiles.length &&
                        tiles[tileType] != null &&
                        tiles[tileType].getImage() != null) {
                    g2.drawImage(
                            tiles[tileType].getImage(),
                            pantallaX,
                            pantallaY,
                            tamanioTile,
                            tamanioTile,
                            null);
                }
            }
        }

        // Draw consumable items
        for (ConsumableItemModel item : new ArrayList<>(itemsConsumibles)) {
            if (!item.isPickedUp()) {
                int pantallaX = item.getX() - camaraX;
                int pantallaY = item.getY() - camaraY;

                // Only draw if on screen
                if (pantallaX + tamanioTile > 0 && pantallaX < anchoPantalla &&
                        pantallaY + tamanioTile > 0 && pantallaY < altoPantalla) {
                    g2.drawImage(item.getSprite(), pantallaX, pantallaY, tamanioTile, tamanioTile, null);
                }
            }
        }
    }

    public String getEstadisticas() {
        return ("Chunks activos: " +
                chunksActivos.size() +
                " | Área: [" +
                minChunkX +
                "," +
                minChunkY +
                "] a [" +
                maxChunkX +
                "," +
                maxChunkY +
                "]");
    }

    public int getChunksActivos() {
        return chunksActivos.size();
    }

    public boolean tieneSolido(int tileX, int tileY) {
        int tileType = getTileEnMundo(tileX, tileY);

        if (tileType >= 0 && tileType < tiles.length && tiles[tileType] != null) {
            return tiles[tileType].isCollision();
        }

        return false;
    }

    public void reiniciarConSeed(long seed) {
        this.chunksActivos.clear();
        this.itemsConsumibles.clear();
        this.generador = new GeneradorMundo(seed, this);
        System.out.println("Mapa reiniciado con nueva seed: " + seed);
    }

    public long getSeed() {
        return generador.getSeed();
    }

    public List<ConsumableItemModel> getItemsConsumibles() {
        return itemsConsumibles;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public int getTamanioTile() {
        return tamanioTile;
    }

    public void agregarItem(ConsumableItemModel item) {
        this.itemsConsumibles.add(item);
    }
}