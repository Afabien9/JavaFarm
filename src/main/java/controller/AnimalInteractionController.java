package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.Animal; // Import indispensable
import main.java.model.AnimalType;
import main.java.model.Enclosure;
import main.java.service.FeedingService;

public class AnimalInteractionController {
    @FXML private Label animalNameLabel;
    @FXML private Label statusLabel;
    @FXML private Label needsLabel;
    @FXML private Button feedButton;

    private Enclosure enclosure;

    public void setEnclosure(Enclosure enc) {
        this.enclosure = enc;


        Animal animal = enc.getCurrentAnimal();
        if (animal == null) return;

        animalNameLabel.setText(getAnimalEmoji(animal.getName()) + " " + animal.getName());


        String food = switch(animal.getType()) {
            case CHICKEN -> "Blé récolté";
            case COW -> "Chou récolté";
            case SHEEP -> "Maïs récolté";
            default -> "Nourriture";
        };

        needsLabel.setText("Nécessite : 1x " + food);
        updateUI();
    }

    private void updateUI() {

        boolean hungry = enclosure.getCurrentAnimal() != null && enclosure.getCurrentAnimal().isHungry();
        statusLabel.setText(hungry ? "État : 😋 Affamé" : "État : ✨ En production...");
        feedButton.setDisable(!hungry);
    }

    private String getAnimalEmoji(String name) {
        if (name == null) return "🐾";
        String n = name.toLowerCase();
        if (n.contains("poule") || n.contains("chicken")) return "🐔";
        if (n.contains("vache") || n.contains("cow")) return "🐄";
        if (n.contains("mouton") || n.contains("sheep")) return "🐑";
        return "🐾";
    }

    @FXML
    private void handleFeed() {

        if (FeedingService.getInstance().tryFeed(enclosure)) {
            updateUI();
        } else {
            statusLabel.setText("❌ Stock insuffisant !");
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleClose() {
        if (animalNameLabel.getScene() != null) {
            animalNameLabel.getScene().getWindow().hide();
        }
    }
}