package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.model.CropType;
import main.java.model.Inventory;
import main.java.model.Plot;
import main.java.service.GameService;
import main.java.service.LevelService;

public class PlantationController {

    @FXML private VBox seedContainer;
    @FXML private Label infoLabel;

    private Inventory inventory;
    private Plot targetPlot;
    private MainController mainController;

    public void setData(Inventory inventory, Plot plot, MainController mainController) {
        this.inventory = inventory;
        this.targetPlot = plot;
        this.mainController = mainController;
        refreshUI();
    }

    private void refreshUI() {
        seedContainer.getChildren().clear();
        boolean hasAnySeeds = false;

        for (CropType type : CropType.values()) {
            int quantity = inventory.getSeeds().getOrDefault(type, 0);
            if (quantity > 0) {
                seedContainer.getChildren().add(createSeedRow(type, quantity));
                hasAnySeeds = true;
            }
        }

        if (!hasAnySeeds) {
            infoLabel.setText("Aucune graine en stock ! Allez à la boutique.");
            infoLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else {
            infoLabel.setText("Choisissez une graine à planter :");
            infoLabel.setStyle("-fx-text-fill: #ecf0f1;");
        }
    }

    private HBox createSeedRow(CropType type, int quantity) {
        HBox row = new HBox(15);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #34495e; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #7f8c8d; -fx-border-radius: 10;");

        // Vérification du niveau
        int playerLevel = LevelService.getInstance().getCurrentLevel();
        boolean isLocked = playerLevel < type.getMinLevel();

        Label nameLabel = new Label(type.getName() + " (x" + quantity + ")");
        nameLabel.setStyle("-fx-text-fill: " + (isLocked ? "#95a5a6;" : "white;") + " -fx-font-weight: bold; -fx-font-size: 14;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button plantBtn = new Button();
        if (isLocked) {
            plantBtn.setText("NIV. " + type.getMinLevel());
            plantBtn.setDisable(true);
            plantBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            plantBtn.setText("PLANTER");
            plantBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            plantBtn.setOnAction(e -> {
                if (inventory.useSeed(type, 1)) {
                    GameService.getInstance().plantCrop(targetPlot, type);
                    if (mainController != null) mainController.refreshInventoryUI();
                    handleClose();
                }
            });
        }

        row.getChildren().addAll(nameLabel, spacer, plantBtn);
        return row;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) seedContainer.getScene().getWindow();
        stage.close();
    }
}