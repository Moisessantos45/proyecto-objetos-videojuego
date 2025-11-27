package domain;

import domain.ManejadorMapaInfinito;
import model.EnemigoModel;

public class ColisionSystem {
    private ManejadorMapaInfinito mapaInfinito;
    private MapaInfinitoAdapter mapaAdapter;
    private EnemigoSystem enemigoSystem;
    private int tamanioTile;

    public ColisionSystem(ManejadorMapaInfinito mapaInfinito, int tamanioTile) {
        this.mapaInfinito = mapaInfinito;
        this.mapaAdapter = null;
        this.enemigoSystem = null;
        this.tamanioTile = tamanioTile;
    }

    public ColisionSystem(MapaInfinitoAdapter mapaAdapter, EnemigoSystem enemigoSystem) {
        this.mapaAdapter = mapaAdapter;
        this.mapaInfinito = mapaAdapter.getMapaOriginal();
        this.enemigoSystem = enemigoSystem;
        this.tamanioTile = mapaAdapter.getTamanioTile();
    }

    public ColisionSystem(MapaInfinitoAdapter mapaAdapter) {
        this(mapaAdapter, null);
    }

    public int verificarColisionTiles(int mundoX, int mundoY, int velocidad, String direccion) {
        int nuevoMundoX = mundoX;
        int nuevoMundoY = mundoY;

        switch (direccion) {
            case "arriba": nuevoMundoY = mundoY - velocidad; break;
            case "abajo": nuevoMundoY = mundoY + velocidad; break;
            case "izquierda": nuevoMundoX = mundoX - velocidad; break;
            case "derecha": nuevoMundoX = mundoX + velocidad; break;
        }

        int margen = 8;
        ManejadorMapaInfinito mapa = mapaAdapter != null ? mapaAdapter.getMapaOriginal() : mapaInfinito;
        
        int izquierdaTile = (nuevoMundoX + margen) / tamanioTile;
        int derechaTile = (nuevoMundoX + tamanioTile - margen - 1) / tamanioTile;
        int arribaTile = (nuevoMundoY + margen) / tamanioTile;
        int abajoTile = (nuevoMundoY + tamanioTile - margen - 1) / tamanioTile;

        int tile1, tile2;

        switch (direccion) {
            case "arriba":
                tile1 = mapa.getTileEnMundo(izquierdaTile, arribaTile);
                tile2 = mapa.getTileEnMundo(derechaTile, arribaTile);
                if (mapa.getTiles()[tile1].isCollision() || mapa.getTiles()[tile2].isCollision()) {
                    return mapa.getTiles()[tile1].isCollision() ? tile1 : tile2;
                }
                break;
            case "abajo":
                tile1 = mapa.getTileEnMundo(izquierdaTile, abajoTile);
                tile2 = mapa.getTileEnMundo(derechaTile, abajoTile);
                if (mapa.getTiles()[tile1].isCollision() || mapa.getTiles()[tile2].isCollision()) {
                    return mapa.getTiles()[tile1].isCollision() ? tile1 : tile2;
                }
                break;
            case "izquierda":
                tile1 = mapa.getTileEnMundo(izquierdaTile, arribaTile);
                tile2 = mapa.getTileEnMundo(izquierdaTile, abajoTile);
                if (mapa.getTiles()[tile1].isCollision() || mapa.getTiles()[tile2].isCollision()) {
                    return mapa.getTiles()[tile1].isCollision() ? tile1 : tile2;
                }
                break;
            case "derecha":
                tile1 = mapa.getTileEnMundo(derechaTile, arribaTile);
                tile2 = mapa.getTileEnMundo(derechaTile, abajoTile);
                if (mapa.getTiles()[tile1].isCollision() || mapa.getTiles()[tile2].isCollision()) {
                    return mapa.getTiles()[tile1].isCollision() ? tile1 : tile2;
                }
                break;
        }

        return -1;
    }

    public static boolean verificarColisionEntidades(int x1, int y1, int ancho1, int alto1,
                                               int x2, int y2, int ancho2, int alto2) {
        return x1 < x2 + ancho2 &&
               x1 + ancho1 > x2 &&
               y1 < y2 + alto2 &&
               y1 + alto1 > y2;
    }
}