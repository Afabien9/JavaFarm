package main.java.model;

/**
 * RÉSOUT L'ERREUR : class WeatherType
 * Définit les types de météo possibles pour le jeu.
 */
public enum WeatherType {
    SUNNY("Soleil", "#FFD700"),
    RAINY("Pluie", "#4682B4"),
    STORM("Orage", "#483D8B"),
    CLOUDY("Nuageux", "#A9A9A9");

    private final String name;
    private final String color;

    WeatherType(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
}