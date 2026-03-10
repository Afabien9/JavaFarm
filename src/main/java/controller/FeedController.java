package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.java.model.AnimalFeedTask;
import main.java.model.Enclosure;

public class FeedController {
    @FXML private Label infoLabel;
    private Enclosure currentEnclosure;

    public void setEnclosure(Enclosure enc) {
        this.currentEnclosure = enc;
        if (enc.getCurrentAnimal() != null) {
            infoLabel.setText("Nourrir : " + enc.getCurrentAnimal().getName());
        }
    }

    @FXML
    private void handleFeed() {
        if (currentEnclosure != null && AnimalFeedTask.tryFeedAnimal(currentEnclosure)) {
            infoLabel.setText("L'animal est nourri !");
        } else {
            infoLabel.setText("Pas de nourriture en stock !");
        }
    }
}