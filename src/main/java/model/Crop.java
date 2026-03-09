package main.java.model;

import javafx.scene.paint.Color;


public class Crop {
    private final String name;
    private final int growthTime; // Temps en secondes avant que l'état passe à READY
    private final int buyPrice;
    private final int sellPrice;
    private CropType type;

    public Crop(String name, int growthTime, int buyPrice, int sellPrice) {
        this.name = name;
        this.growthTime = growthTime;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    /**
     * Retourne la couleur spécifique à afficher quand la plante est mûre.
     */
    public Color getReadyColor() {
        if (name == null) return Color.GOLD;

        return switch (name) {
            case "Blé" -> Color.GOLD;
            case "Maïs" -> Color.YELLOW;
            case "Tomate" -> Color.TOMATO;
            default -> Color.GOLD;
        };
    }

    // --- GETTERS ET SETTERS ---

    public String getName() {
        return name;
    }

    public int getGrowthTime() {
        return growthTime;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public CropType getType() {
        return type;
    }

    public void setType(CropType type) {
        this.type = type;
    }
}