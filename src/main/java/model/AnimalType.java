package main.java.model;

import javafx.scene.paint.Color;

/**
 * Enumération des types d'animaux.
 * Ajout des méthodes getProduct() et getFoodCost() réclamées par le compilateur.
 */
public enum AnimalType {
    CHICKEN("Poule", 50, "Oeuf", 10, 5, 2, Color.WHITE),
    COW("Vache", 200, "Lait", 20, 30, 10, Color.BLACK),
    SHEEP("Mouton", 150, "Laine", 25, 20, 5, Color.LIGHTGRAY);

    private final String name;
    private final int buyPrice;
    private final String productName;
    private final int productionTime;
    private final int productValue;
    private final int foodCost; // Coût de nourriture par cycle de production
    private final Color animalColor;

    AnimalType(String name, int buyPrice, String productName, int productionTime, int productValue, int foodCost, Color animalColor) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.productName = productName;
        this.productionTime = productionTime;
        this.productValue = productValue;
        this.foodCost = foodCost;
        this.animalColor = animalColor;
    }

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }

    /**
     * Retourne le nom du produit (ex: "Oeuf").
     * Le compilateur cherchait ce nom précis : getProduct().
     */
    public String getProduct() {
        return productName;
    }

    public int getProductionTime() { return productionTime; }
    public int getProductValue() { return productValue; }

    /**
     * Retourne le coût en nourriture.
     * Le compilateur cherchait ce nom précis : getFoodCost().
     */
    public int getFoodCost() {
        return foodCost;
    }

    public Color getAnimalColor() { return animalColor; }
}