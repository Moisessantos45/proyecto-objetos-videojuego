package model;

public class Chunk {

    public static final int CHUNK_SIZE = 16;

    private int chunkX;
    private int chunkY;
    private int[][] tiles;
    private boolean generado;
    private long ultimoAcceso;

    public Chunk(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.tiles = new int[CHUNK_SIZE][CHUNK_SIZE];
        this.generado = false;
        this.ultimoAcceso = System.currentTimeMillis();
    }

    public int getTile(int localX, int localY) {
        if (
            localX >= 0 &&
            localX < CHUNK_SIZE &&
            localY >= 0 &&
            localY < CHUNK_SIZE
        ) {
            this.ultimoAcceso = System.currentTimeMillis();
            return tiles[localY][localX];
        }
        return 0;
    }

    public void setTile(int localX, int localY, int tileType) {
        if (
            localX >= 0 &&
            localX < CHUNK_SIZE &&
            localY >= 0 &&
            localY < CHUNK_SIZE
        ) {
            tiles[localY][localX] = tileType;
            this.ultimoAcceso = System.currentTimeMillis();
        }
    }

    public int[][] getTiles() {
        this.ultimoAcceso = System.currentTimeMillis();
        return tiles;
    }

    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
        this.ultimoAcceso = System.currentTimeMillis();
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public boolean isGenerado() {
        return generado;
    }

    public void setGenerado(boolean generado) {
        this.generado = generado;
    }

    public long getUltimoAcceso() {
        return ultimoAcceso;
    }

    public String getKey() {
        return chunkX + "," + chunkY;
    }

    public static String crearKey(int chunkX, int chunkY) {
        return chunkX + "," + chunkY;
    }
}
