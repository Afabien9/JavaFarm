package main.java.model;

import javafx.scene.paint.Color;

public enum CropType {
    WHEAT("Blé", 10, 5, Color.GOLD),
    CORN("Maïs", 20, 10, Color.YELLOW),
    TOMATO("Tomate", 30, 15, Color.RED),
    CARROT("Carotte", 15, 7, Color.ORANGE),
    CABBAGE("Chou", 25, 12, Color.LIGHTGREEN);

    private final String name;
    private final int buyPrice;
    private final int growthTime;
    private final Color readyColor;

    CropType(String name, int buyPrice, int growthTime, Color readyColor) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.growthTime = growthTime;
        this.readyColor = readyColor;
    }

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public int getGrowthTime() { return growthTime; }
    public Color getReadyColor() { return readyColor; }

    @Override
    public String toString() { return name; }
}