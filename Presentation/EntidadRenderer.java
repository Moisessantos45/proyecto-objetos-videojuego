package Presentation;

import java.awt.Graphics2D;
import model.EntidadModel;
import model.SpriteData;
import java.awt.image.BufferedImage;

public class EntidadRenderer {
    private int tamanioTile;

    public EntidadRenderer(int tamanioTile) {
        this.tamanioTile = tamanioTile;
    }

    public void render(Graphics2D g2, EntidadModel entidad, int pantallaX, int pantallaY) {
        SpriteData spriteData = entidad.getSpriteData();
        BufferedImage sprite = spriteData.getSpriteActual();

        if (sprite != null) {
            g2.drawImage(sprite, pantallaX, pantallaY, tamanioTile, tamanioTile, null);
        } else {
            g2.setColor(java.awt.Color.RED);
            g2.fillRect(pantallaX, pantallaY, tamanioTile, tamanioTile);
            System.out.println("Advertencia: sprite nulo en EntidadRenderer");
        }
    }

    public void renderConVida(Graphics2D g2, EntidadModel entidad, int pantallaX, int pantallaY) {
        render(g2, entidad, pantallaX, pantallaY);
        
        int barraAncho = tamanioTile;
        int barraAlto = 4;
        int barraX = pantallaX;
        int barraY = pantallaY - 8;
        
        g2.setColor(java.awt.Color.RED);
        g2.fillRect(barraX, barraY, barraAncho, barraAlto);
        
        int vidaActual = (int) ((entidad.getVida() / 100.0) * barraAncho);
        g2.setColor(java.awt.Color.GREEN);
        g2.fillRect(barraX, barraY, vidaActual, barraAlto);
    }
}
