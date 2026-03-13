package main.java.model;

import javafx.scene.paint.Color;

public enum AnimalType {
    CHICKEN("Poule", 50, "Oeuf", 10, 5, 2, Color.WHITE, 1),
    SHEEP("Mouton", 150, "Laine", 25, 20, 5, Color.LIGHTGRAY, 3),
    COW("Vache", 200, "Lait", 20, 30, 10, Color.BLACK, 5);

    private final String name;
    private final int buyPrice;
    private final String productName;
    private final int productionTime;
    private final int productValue;
    private final int foodCost;
    private final Color animalColor;
    private final int requiredLevel;

    AnimalType(String name, int buyPrice, String productName, int productionTime, int productValue, int foodCost,
               Color animalColor, int requiredLevel) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.productName = productName;
        this.productionTime = productionTime;
        this.productValue = productValue;
        this.foodCost = foodCost;
        this.animalColor = animalColor;
        this.requiredLevel = requiredLevel;
    }

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public String getProduct() { return productName; }
    public int getProductionTime() { return productionTime; }
    public int getProductValue() { return productValue; }
    public int getFoodCost() { return foodCost; }
    public Color getAnimalColor() { return animalColor; }
    public int getRequiredLevel() { return requiredLevel; }

    public boolean isHungry() { return true; }
    public void feed() {}
    public boolean isReadyToProduce() { return false; }
    public void resetProduction() {}
    public String getProducedProductName() { return productName; }
}