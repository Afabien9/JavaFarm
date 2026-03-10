package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import main.java.model.Inventory;

public class InventoryController {
    @FXML private ListView<String> inventoryListView;
    private MainController mainCtrl;

    public void setData(Inventory inventory, MainController mainCtrl) {
        this.mainCtrl = mainCtrl;
        inventoryListView.getItems().clear();

        inventory.getSeeds().forEach((type, qty) -> {
            if (qty > 0) inventoryListView.getItems().add("🌱 Graine: " + type.getName() + " (x" + qty + ")");
        });

        inventory.getProducts().forEach((type, qty) -> {
            if (qty > 0) inventoryListView.getItems().add("📦 Récolte: " + type.getName() + " (x" + qty + ")");
        });

        inventory.getAnimals().forEach((type, qty) -> {
            if (qty > 0) inventoryListView.getItems().add("🐾 Animal à placer: " + type.getName() + " (x" + qty + ")");
        });

        inventory.getAnimalProducts().forEach((name, qty) -> {
            if (qty > 0) inventoryListView.getItems().add("✨ Produit: " + name + " (x" + qty + ")");
        });

        if (inventoryListView.getItems().isEmpty()) {
            inventoryListView.getItems().add("Votre grange est vide...");
        }
    }

    @FXML
    private void handleSellAll() {
        if (mainCtrl != null) {
            mainCtrl.sellAll();
            handleClose();
        }
    }

    @FXML
    private void handleClose() {
        if (inventoryListView.getScene() != null) {
            inventoryListView.getScene().getWindow().hide();
        }
    }
}