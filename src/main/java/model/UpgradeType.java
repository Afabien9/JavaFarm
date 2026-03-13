package main.java.model;

public enum UpgradeType {
    CROP_SPEED("Serre Automatisée", 500, "Vitesse de pousse +25%", 0.75, 2),
    CROP_YIELD("Engrais Bio", 750, "Prix de vente +20%", 1.20, 5),
    ANIMAL_SPEED("Musique Relaxante", 600, "Production animale +30% rapide", 0.70, 7),
    ANIMAL_VALUE("Label Qualité", 1000, "Valeur des produits +25%", 1.25, 8);

    private final String name;
    private final int price;
    private final String description;
    private final double multiplier;
    private final int minLevel;

    UpgradeType(String name, int price, String description, double multiplier, int minLevel) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.multiplier = multiplier;
        this.minLevel = minLevel;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
    public double getMultiplier() { return multiplier; }
    public int getMinLevel() { return minLevel; }
}