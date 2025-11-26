package model;

import java.awt.image.BufferedImage;

public class TileModel {
    private BufferedImage image;
    private boolean colision;

    public TileModel() {
        this.colision = false;
    }

    public TileModel(BufferedImage image, boolean colision) {
        this.image = image;
        this.colision = colision;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean isColision() {
        return colision;
    }

    public void setColision(boolean colision) {
        this.colision = colision;
    }
}
