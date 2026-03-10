package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.model.AnimalType;
import main.java.model.Enclosure;
import main.java.model.Inventory;

public class AnimalPlacementController {

    @FXML private VBox animalContainer;
    @FXML private Label infoLabel;

    private Inventory inventory;
    private Enclosure targetEnclosure;
    private MainController mainController;

    public void setData(Inventory inventory, Enclosure enclosure, MainController mainController) {
        this.inventory = inventory;
        this.targetEnclosure = enclosure;
        this.mainController = mainController;
        refreshUI();
    }

    private void refreshUI() {
        animalContainer.getChildren().clear();
        boolean hasAnimals = false;

        for (AnimalType type : AnimalType.values()) {
            int qty = inventory.getAnimals().getOrDefault(type, 0);
            if (qty > 0) {
                animalContainer.getChildren().add(createAnimalRow(type, qty));
                hasAnimals = true;
            }
        }

        if (!hasAnimals) {
            infoLabel.setText("Aucun animal en stock ! Visitez la boutique.");
            infoLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private HBox createAnimalRow(AnimalType type, int qty) {
        HBox row = new HBox(15);
        row.setStyle("-fx-background-color: #34495e; -fx-padding: 10; -fx-background-radius: 10;");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label label = new Label(type.getName() + " (x" + qty + ")");
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button placeBtn = new Button("PLACER");
        placeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");

        placeBtn.setOnAction(e -> {
            if (inventory.useAnimal(type)) {
                targetEnclosure.addAnimal(type);
                if (mainController != null) mainController.refreshInventoryUI();
                handleClose();
            }
        });

        row.getChildren().addAll(label, spacer, placeBtn);
        return row;
    }

    @FXML private void handleClose() {
        ((Stage) animalContainer.getScene().getWindow()).close();
    }
}