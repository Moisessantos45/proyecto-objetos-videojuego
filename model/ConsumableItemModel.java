package model;

import java.awt.image.BufferedImage;

public class ConsumableItemModel {
    public enum ItemType {
        POTION, POISON
    }

    private int x, y; // Posición en el mundo
    private ItemType type; // Tipo de item (pocion o veneno)
    private double effectValue; // Cantidad de curación o daño
    private int quantity; // Cantidad de pociones (para veneno siempre 1)
    private boolean isPickedUp; // Si ha sido recogido del mapa
    private BufferedImage sprite; // Sprite del item

    private int variant; // Para diferenciar entre tipos de pociones, por ejemplo

    public ConsumableItemModel(int x, int y, ItemType type, double effectValue, int quantity, BufferedImage sprite, int variant) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.effectValue = effectValue;
        this.quantity = quantity;
        this.isPickedUp = false;
        this.sprite = sprite;
        this.variant = variant;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public ItemType getType() { return type; }
    public double getEffectValue() { return effectValue; }
    public int getQuantity() { return quantity; }
    public boolean isPickedUp() { return isPickedUp; }
    public BufferedImage getSprite() { return sprite; }
    public int getVariant() { return variant; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setType(ItemType type) { this.type = type; }
    public void setEffectValue(double effectValue) { this.effectValue = effectValue; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPickedUp(boolean isPickedUp) { this.isPickedUp = isPickedUp; }
    public void setSprite(BufferedImage sprite) { this.sprite = sprite; }
    public void setVariant(int variant) { this.variant = variant; }
}