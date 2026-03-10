package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.model.*;
import main.java.service.GameService;
import java.io.IOException;

public class MainController {

    public enum StructureType { PLOT, ENCLOSURE }

    @FXML private ScrollPane mapScroll;
    @FXML private Label statusLabel;
    @FXML private HBox buildToolbar;

    @FXML private SidebarController sidebarController;
    @FXML private MapController mapController;

    private final Wallet wallet = new Wallet();
    private final Inventory inventory = new Inventory();

    private boolean buildModeActive = false;
    private StructureType activeStructureType = StructureType.PLOT;

    @FXML
    public void initialize() {
        GameService.getInstance().setInventory(this.inventory);
        GameService.getInstance().setWallet(this.wallet);

        if (sidebarController != null) {
            sidebarController.setMainController(this);
            sidebarController.setupData(wallet, inventory);
        }
        if (mapController != null) {
            mapController.setMainController(this);
            mapController.setupData(wallet, inventory);
        }

        javafx.application.Platform.runLater(() -> {
            if (mapScroll != null) mapScroll.requestFocus();
        });
    }

    public void setStructureToBuild(StructureType type) {
        this.activeStructureType = type;
        if (statusLabel != null) {
            statusLabel.setText("Construction : " + (type == StructureType.PLOT ? "CHAMPS" : "ENCLOS"));
        }
    }

    public void toggleBuildMode() {
        this.buildModeActive = !this.buildModeActive;
        if (buildToolbar != null) {
            buildToolbar.setVisible(buildModeActive);
        }
        if (statusLabel != null) {
            statusLabel.setText(buildModeActive ? "MODE CONSTRUCTION ACTIF" : "MODE INTERACTION");
        }
    }

    public boolean isBuildModeActive() { return buildModeActive; }
    public StructureType getActiveStructureType() { return activeStructureType; }

    @FXML public void handleModePlots() { setStructureToBuild(StructureType.PLOT); }
    @FXML public void handleModeEnclosures() { setStructureToBuild(StructureType.ENCLOSURE); }

    @FXML
    public void handleKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case M: toggleDebugMode(); break;
            case B: toggleBuildMode(); break;
            default: break;
        }
    }

    public void sellCrops(CropType type, int qty) {
        if (type == null) return;
        int stock = inventory.getProducts().getOrDefault(type, 0);
        int amount = (qty == -1) ? stock : Math.min(qty, stock);
        if (amount > 0 && inventory.useProduct(type, amount)) {
            wallet.addMoney((int)(type.getBuyPrice() * 1.5) * amount);
            refreshInventoryUI();
        }
    }

    public void sellAnimalProducts() {
        if (inventory == null || wallet == null) return;
        inventory.getAnimalProducts().forEach((productName, qty) -> {
            if (qty > 0) {
                for (AnimalType type : AnimalType.values()) {
                    if (type.getProduct().equalsIgnoreCase(productName)) {
                        wallet.addMoney(type.getProductValue() * qty);
                        break;
                    }
                }
            }
        });
        inventory.clearAnimalProducts();
        refreshInventoryUI();
    }

    public void sellAll() {
        for (CropType type : CropType.values()) {
            sellCrops(type, -1);
        }
        sellAnimalProducts();
    }

    public void toggleDebugMode() {
        GameService.getInstance().toggleDebug();
        boolean isActive = GameService.getInstance().isDebugActive();
        if (statusLabel != null) {
            statusLabel.setText(isActive ? "DEBUG : ACTIF (+1000€)" : "DEBUG : DÉSACTIVÉ");
        }
        if (isActive) wallet.addMoney(1000);
        refreshInventoryUI();
    }

    public void openShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShopView.fxml"));
            Parent root = loader.load();
            ShopController shopCtrl = loader.getController();
            shopCtrl.setData(wallet, inventory, this);

            showPopup(root, "Boutique de la Ferme");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void handleOpenInventory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InventoryView.fxml"));
            Parent root = loader.load();
            InventoryController invCtrl = loader.getController();
            invCtrl.setData(inventory, this);

            showPopup(root, "Ma Grange (Inventaire)");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void openPlantingMenu(Plot plot) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlantationView.fxml"));
            Parent root = loader.load();
            PlantationController plantCtrl = loader.getController();
            plantCtrl.setData(inventory, plot, this);

            showPopup(root, "Semer des graines");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showPopup(Parent root, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        // Ajout du CSS pour que les boutons personnalisés s'affichent bien dans les popups
        if (getClass().getResource("/css/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        }
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void refreshInventoryUI() {
        if (sidebarController != null) sidebarController.updateInventoryDisplay();
    }

    public void updateWeatherUI(String w) {
        if (sidebarController != null) sidebarController.updateWeatherDisplay(w);
    }

    @FXML public void handleExit(ActionEvent event) { System.exit(0); }
}