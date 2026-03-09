package main.java.model;

public enum Season {
    PRINTEMPS("Printemps", "#2ecc71"), // Vert
    ÉTÉ("Été", "#f1c40f"),            // Jaune
    AUTOMNE("Automne", "#e67e22"),     // Orange
    HIVER("Hiver", "#3498db");        // Bleu

    private final String label;
    private final String color;

    Season(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() { return label; }
    public String getColor() { return color; }
}