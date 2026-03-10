package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        animalNameLabel.setText(enc.getCurrentAnimal().getName());

        // Affichage des besoins selon l'animal
        String food = switch(enc.getCurrentAnimal()) {
            case CHICKEN -> "Graines de Blé";
            case COW -> "Maïs récolté";
            case SHEEP -> "Blé récolté";
        };
        needsLabel.setText("Nécessite : 1x " + food);

        updateUI();
    }

    private void updateUI() {
        boolean hungry = enclosure.isHungryProperty().get();
        statusLabel.setText(hungry ? "État : Affamé" : "État : En production...");
        feedButton.setDisable(!hungry);
    }

    @FXML
    private void handleFeed() {
        if (FeedingService.getInstance().tryFeed(enclosure)) {
            updateUI();
        } else {
            statusLabel.setText("Stock insuffisant !");
        }
    }
}