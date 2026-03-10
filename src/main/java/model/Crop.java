package main.java.model;

import javafx.scene.paint.Color;

public class Crop {
    private final String name;
    private final int growthTime;
    private final int buyPrice;
    private final int sellPrice;
    private CropType type;

    // Nouveau constructeur pour accepter CropType
    public Crop(CropType type) {
        this.type = type;
        this.name = type.getName();
        this.growthTime = (int) type.getGrowthTime();
        this.buyPrice = type.getBuyPrice();
        this.sellPrice = (int) (type.getBuyPrice() * 1.5);
    }

    public Crop(String name, int growthTime, int buyPrice, int sellPrice) {
        this.name = name;
        this.growthTime = growthTime;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public Color getReadyColor() {
        if (name == null) return Color.GOLD;

        return switch (name) {
            case "Blé" -> Color.GOLD;
            case "Maïs" -> Color.YELLOW;
            case "Tomate" -> Color.TOMATO;
            default -> Color.GOLD;
        };
    }

    public String getName() { return name; }
    public int getGrowthTime() { return growthTime; }
    public int getBuyPrice() { return buyPrice; }
    public int getSellPrice() { return sellPrice; }
    public CropType getType() { return type; }
    public void setType(CropType type) { this.type = type; }
}