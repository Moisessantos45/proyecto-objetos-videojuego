package domain;

import domain.ManejadorMapaInfinito;
import java.awt.Graphics2D;

/**
 * Adaptador para ManejadorMapaInfinito.
 */
public class MapaInfinitoAdapter {
    private ManejadorMapaInfinito mapaOriginal;
    private int tamanioTile;

    public MapaInfinitoAdapter(int tamanioTile, int anchoPantalla, int altoPantalla) {
        this.tamanioTile = tamanioTile;
        long seed = System.currentTimeMillis();
        this.mapaOriginal = new ManejadorMapaInfinito(tamanioTile, anchoPantalla, altoPantalla, seed);
    }

    public MapaInfinitoAdapter(int tamanioTile, int anchoPantalla, int altoPantalla, long seed) {
        this.tamanioTile = tamanioTile;
        this.mapaOriginal = new ManejadorMapaInfinito(tamanioTile, anchoPantalla, altoPantalla, seed);
    }

    public void actualizarChunksActivos(int mundoX, int mundoY) {
        mapaOriginal.actualizarChunksActivos(mundoX, mundoY);
    }

    public void draw(Graphics2D g2, int camaraX, int camaraY) {
        mapaOriginal.draw(g2, camaraX, camaraY);
    }
	
    public boolean tieneSolido(int tileX, int tileY) {
        return mapaOriginal.tieneSolido(tileX, tileY);
    }

    public String getEstadisticas() {
        return mapaOriginal.getEstadisticas();
    }

    public ManejadorMapaInfinito getMapaOriginal() {
        return mapaOriginal;
    }

    public int getTamanioTile() {
        return tamanioTile;
    }
}