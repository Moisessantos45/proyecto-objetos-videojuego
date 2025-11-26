package domain;

import model.SpriteData;

public class AnimacionSystem implements IUpdateable {
    private SpriteData spriteData;

    public AnimacionSystem(SpriteData spriteData) {
        this.spriteData = spriteData;
    }

    @Override
    public void update() {
        int contador = spriteData.getContadorSprites();
        contador++;

        if (contador > spriteData.getCambioSprite()) {
            int numeroSprite = spriteData.getNumeroSprite();
            if (numeroSprite == 1) {
                spriteData.setNumeroSprite(2);
            } else {
                spriteData.setNumeroSprite(1);
            }
            contador = 0;
        }

        spriteData.setContadorSprites(contador);
    }

    public void reiniciarAnimacion() {
        spriteData.setContadorSprites(0);
        spriteData.setNumeroSprite(1);
    }
}
