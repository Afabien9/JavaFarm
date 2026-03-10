package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.*;

public class PlantationController {
    @FXML private ListView<CropType> seedsInInventoryList;
    private Inventory inventory;
    private Plot plot;
    private MainController mainCtrl;

    public void setData(Inventory inv, Plot plot, MainController main) {
        this.inventory = inv;
        this.plot = plot;
        this.mainCtrl = main;

        if (seedsInInventoryList != null) {
            seedsInInventoryList.getItems().clear();
            inv.getSeeds().forEach((type, qty) -> {
                if (qty > 0) seedsInInventoryList.getItems().add(type);
            });

            seedsInInventoryList.setCellFactory(lv -> new ListCell<CropType>() {
                @Override
                protected void updateItem(CropType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        int stock = inventory.getSeeds().getOrDefault(item, 0);
                        setText(item.getName() + " (Stock: " + stock + ")");
                    }
                }
            });
        }
    }

    @FXML
    private void handlePlant() {
        CropType selected = seedsInInventoryList.getSelectionModel().getSelectedItem();

        // On vérifie que la sélection existe et qu'il y a du stock
        if (selected != null && inventory.getSeeds().getOrDefault(selected, 0) > 0) {
            if (inventory.useSeed(selected)) {
                plot.plant(selected);
                if (mainCtrl != null) {
                    mainCtrl.refreshInventoryUI();
                }
                handleCancel();
            }
        }
    }

    @FXML
    private void handleCancel() {
        if (seedsInInventoryList != null && seedsInInventoryList.getScene() != null) {
            seedsInInventoryList.getScene().getWindow().hide();
        }
    }
}