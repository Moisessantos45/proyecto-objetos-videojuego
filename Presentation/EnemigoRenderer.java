package Presentation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.EnemigoModel;

public class EnemigoRenderer {
    private int tamanioTile;
    
    public EnemigoRenderer(int tamanioTile) {
        this.tamanioTile = tamanioTile;
    }
    
    public void render(Graphics2D g2, EnemigoModel enemigo, int camaraX, int camaraY) {
        BufferedImage imagen = enemigo.getFrameActualImagen();
        
        if (imagen != null) {
            int pantallaX = enemigo.getTransform().getX() - camaraX;
            int pantallaY = enemigo.getTransform().getY() - camaraY;
            
            g2.drawImage(imagen, pantallaX, pantallaY, tamanioTile, tamanioTile, null);
        }
    }
    
    public void renderTodos(Graphics2D g2, java.util.List<EnemigoModel> enemigos, int camaraX, int camaraY) {
        for (EnemigoModel enemigo : enemigos) {
            render(g2, enemigo, camaraX, camaraY);
        }
    }
}
