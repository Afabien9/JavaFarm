package main.java.model;

import javafx.scene.paint.Color;

public enum CropType {
    WHEAT("Blé", 10, 5, Color.GOLD),
    CORN("Maïs", 20, 10, Color.YELLOW),
    TOMATO("Tomate", 30, 15, Color.RED);

    private final String name;
    private final int buyPrice;
    private final int growthTime; // C'est cette donnée qui te manquait
    private final Color readyColor;

    CropType(String name, int buyPrice, int growthTime, Color readyColor) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.growthTime = growthTime;
        this.readyColor = readyColor;
    }

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public int getGrowthTime() { return growthTime; } // LE SYMBOLE MANQUANT
    public Color getReadyColor() { return readyColor; }
}