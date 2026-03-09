package main.java.model;

public enum AnimalType {
    CHICKEN("Poule", 50, 5, "Oeuf", 10),
    PIG("Cochon", 150, 15, "Viande", 40),
    COW("Vache", 300, 25, "Lait", 70);

    private final String name;
    private final int buyPrice;
    private final int foodCost;
    private final String product;
    private final int productValue;

    AnimalType(String name, int buyPrice, int foodCost, String product, int productValue) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.foodCost = foodCost;
        this.product = product;
        this.productValue = productValue;
    }

    public String getName() { return name; }
    public int getBuyPrice() { return buyPrice; }
    public int getFoodCost() { return foodCost; }
    public String getProduct() { return product; }
    public int getProductValue() { return productValue; }
}