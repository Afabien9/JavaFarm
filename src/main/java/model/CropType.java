package main.java.model;

import javafx.scene.paint.Color;

public enum CropType {

    WHEAT("Blé", 10, 5, Color.GOLD, 1),
    CORN("Maïs", 20, 10, Color.YELLOW, 2),
    CARROT("Carotte", 30, 15, Color.ORANGE, 3),
    CABBAGE("Chou", 45, 20, Color.LIGHTGREEN, 4);

    private final String name;
    private final int buyPrice;
    private final int growthTime;
    private final Color readyColor;
    private final int minLevel;


    private double sellMultiplier = 1.5;

    CropType(String name, int buyPrice, int growthTime, Color readyColor, int minLevel) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.growthTime = growthTime;
        this.readyColor = readyColor;
        this.minLevel = minLevel;
    }


    public int getSellPrice() {
        return (int) (buyPrice * sellMultiplier);
    }


    public void increaseValue() {
        if (this.sellMultiplier < 4.0) {
            this.sellMultiplier += 0.01;
        }
    }

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public int getGrowthTime() { return growthTime; }
    public Color getReadyColor() { return readyColor; }
    public int getMinLevel() { return minLevel; }

    @Override
    public String toString() { return name; }
}