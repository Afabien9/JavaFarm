package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.model.*;
import main.java.service.SelectionService;

public class SidebarController {

    @FXML private Label walletLabel;
    @FXML private Label weatherLabel;
    @FXML private ListView<String> inventoryListView;

    private MainController mainCtrl;
    private Inventory inventory;
    private final ObservableList<String> inventoryItems = FXCollections.observableArrayList();

    public void setMainController(MainController m) {
        this.mainCtrl = m;
    }

    public void setupData(Wallet w, Inventory i) {
        this.inventory = i;
        if (walletLabel != null && w != null) {
            walletLabel.textProperty().bind(w.moneyProperty().asString("Argent: %d €"));
        }

        if (inventoryListView != null) {
            inventoryListView.setItems(inventoryItems);

            // Écouteur pour la sélection de graines à planter
            inventoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    CropType type = getCropTypeFromText(newVal);
                    SelectionService.getInstance().setSelectedCrop(type);
                }
            });
        }
        updateInventoryDisplay();
    }

    /**
     * Cette méthode est celle que le FXML cherche (onAction="#handleSell")
     */
    @FXML
    private void handleSell() {
        if (mainCtrl != null) {
            // On récupère l'élément sélectionné dans la liste
            CropType selected = getSelectedCropFromList();
            if (selected != null) {
                // On demande au MainController de vendre 1 unité
                mainCtrl.sellCrops(selected, 1);
            } else {
                System.out.println("INFO : Sélectionnez un produit dans l'inventaire pour le vendre.");
            }
        }
    }

    /**
     * Vend tout le stock (onAction="#sellAll")
     */
    @FXML
    private void sellAll() {
        if (mainCtrl != null && inventory != null) {
            inventory.getProducts().keySet().forEach(type -> mainCtrl.sellCrops(type, -1));
        }
    }

    @FXML
    private void handleOpenShop() {
        if (mainCtrl != null) mainCtrl.openShop();
    }

    @FXML
    private void handleToggleBuild() {
        if (mainCtrl != null) mainCtrl.toggleBuildMode();
    }

    /**
     * Active ou désactive le mode debug via le bouton de la sidebar.
     */
    @FXML
    public void handleOpenDebug(ActionEvent actionEvent) {
        if (mainCtrl != null) {
            mainCtrl.toggleDebugMode();
        }
    }

    public void updateWeatherDisplay(String text) {
        if (weatherLabel != null) {
            weatherLabel.setText("MÉTÉO : " + text.toUpperCase());
        }
    }

    public void updateInventoryDisplay() {
        inventoryItems.clear();
        if (inventory != null) {
            // Affichage des graines
            inventory.getSeeds().forEach((type, qty) -> {
                if (qty > 0) inventoryItems.add(type.getName() + " (Graines: " + qty + ")");
            });
            // Affichage des produits récoltés
            inventory.getProducts().forEach((type, qty) -> {
                if (qty > 0) inventoryItems.add(type.getName() + " (Récolte: " + qty + ")");
            });
        }
    }

    private CropType getSelectedCropFromList() {
        String selected = inventoryListView.getSelectionModel().getSelectedItem();
        return (selected != null) ? getCropTypeFromText(selected) : null;
    }

    private CropType getCropTypeFromText(String text) {
        if (text.contains("Blé")) return CropType.WHEAT;
        if (text.contains("Maïs")) return CropType.CORN;
        if (text.contains("Tomate")) return CropType.TOMATO;
        return null;
    }
}