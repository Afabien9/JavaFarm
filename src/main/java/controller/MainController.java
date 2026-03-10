package main.java.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.java.model.*;
import main.java.service.GameService;
import main.java.service.LevelService;
import main.java.service.UpgradeService;
import java.io.IOException;

public class MainController {

    public enum StructureType { PLOT, ENCLOSURE }

    @FXML private ScrollPane mapScroll;
    @FXML private Label statusLabel;
    @FXML private HBox buildToolbar;
    @FXML private Pane modeOverlay;

    @FXML private SidebarController sidebarController;
    @FXML private MapController mapController;

    private final Wallet wallet = new Wallet();
    private final Inventory inventory = new Inventory();

    private boolean buildModeActive = false;
    private boolean destructionModeActive = false;
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

        Platform.runLater(() -> {
            if (mapScroll != null) mapScroll.requestFocus();
            updateStatusLabel();
        });
    }

    public void showLevelUpNotification(int newLevel) {
        Platform.runLater(() -> {
            Label label = new Label("⭐ NIVEAU SUPÉRIEUR : " + newLevel + " ⭐");
            label.setStyle("-fx-background-color: rgba(241, 196, 15, 0.9); -fx-text-fill: #2c3e50; " +
                    "-fx-font-size: 30; -fx-font-weight: bold; -fx-padding: 20 40; " +
                    "-fx-background-radius: 50; -fx-border-color: white; -fx-border-radius: 50; -fx-border-width: 3;");

            StackPane root = (StackPane) mapScroll.getScene().getRoot();
            StackPane.setAlignment(label, Pos.CENTER);
            root.getChildren().add(label);

            FadeTransition ft = new FadeTransition(Duration.seconds(3), label);
            ft.setFromValue(1.0); ft.setToValue(0.0); ft.setDelay(Duration.seconds(1));
            ft.setOnFinished(e -> root.getChildren().remove(label));
            ft.play();

            updateStatusLabel();
        });
    }

    // --- GESTION DES MODES ---

    public void toggleBuildMode() {
        this.buildModeActive = !this.buildModeActive;
        this.destructionModeActive = false;
        updateStatusLabel();
    }

    public void toggleDestructionMode() {
        this.destructionModeActive = !this.destructionModeActive;
        this.buildModeActive = false;
        updateStatusLabel();
    }

    public void updateStatusLabel() {
        if (statusLabel == null || modeOverlay == null) return;

        if (destructionModeActive) {
            statusLabel.setText("⚠️ MODE DESTRUCTION (CLIQUEZ POUR SUPPRIMER)");
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            modeOverlay.setStyle("-fx-border-width: 15; -fx-border-color: linear-gradient(from 0% 0% to 10% 10%, repeat, #e74c3c 0%, #e74c3c 50%, #1a1a1a 50%, #1a1a1a 100%);");
        }
        else if (buildModeActive) {
            int current = (activeStructureType == StructureType.PLOT) ? mapController.getCurrentPlotCount() : mapController.getCurrentEnclosureCount();
            int max = (activeStructureType == StructureType.PLOT) ? LevelService.getInstance().getMaxPlots() : LevelService.getInstance().getMaxEnclosures();
            String name = (activeStructureType == StructureType.PLOT) ? "CHAMPS" : "ENCLOS";

            statusLabel.setText("MODE CONSTRUCTION : " + name + " (" + current + "/" + max + ")");
            statusLabel.setStyle(current >= max ? "-fx-text-fill: #e67e22; -fx-font-weight: bold;" : "-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            modeOverlay.setStyle("-fx-border-width: 15; -fx-border-color: #7f8c8d; -fx-border-style: solid;");
        }
        else {
            statusLabel.setText("MODE INTERACTION");
            statusLabel.setStyle("-fx-text-fill: white;");
            modeOverlay.setStyle("-fx-border-color: transparent;");
        }
        if (buildToolbar != null) buildToolbar.setVisible(buildModeActive);
    }

    // --- NAVIGATION ---

    public void openShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShopView.fxml"));
            Parent root = loader.load();
            ((ShopController)loader.getController()).setData(wallet, inventory, this);
            showPopup(root, "Boutique");
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * MÉTHODE RÉTABLIE : Ouvre le menu des améliorations
     */
    public void openUpgradeMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpgradeView.fxml"));
            Parent root = loader.load();
            // On suppose que UpgradeController existe et a une méthode setData
            ((UpgradeController)loader.getController()).setData(wallet, this);
            showPopup(root, "Améliorations");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void openPlantingMenu(Plot plot) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlantationView.fxml"));
            Parent root = loader.load();
            ((PlantationController)loader.getController()).setData(inventory, plot, this);
            showPopup(root, "Semer");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void openAnimalPlacementMenu(Enclosure enclosure) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AnimalPlacementView.fxml"));
            Parent root = loader.load();
            ((AnimalPlacementController)loader.getController()).setData(inventory, enclosure, this);
            showPopup(root, "Placer Animal");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void handleOpenInventory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InventoryView.fxml"));
            Parent root = loader.load();
            ((InventoryController)loader.getController()).setData(inventory, this);
            showPopup(root, "Grange");
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- ACTIONS CLAVIER & LOGIQUE ---

    public void sellAll() {
        for (CropType type : CropType.values()) sellCrops(type, -1);
        sellAnimalProducts();
    }

    public void sellCrops(CropType type, int qty) {
        if (type == null) return;
        int stock = inventory.getProducts().getOrDefault(type, 0);
        int amount = (qty == -1) ? stock : Math.min(qty, stock);
        if (amount > 0 && inventory.useProduct(type, amount)) {
            int totalGain = (int)(type.getBuyPrice() * 1.5 * amount);
            wallet.addMoney(totalGain);
            refreshInventoryUI();
        }
    }

    public void sellAnimalProducts() {
        inventory.getAnimalProducts().forEach((name, qty) -> {
            if (qty > 0) wallet.addMoney(50 * qty);
        });
        inventory.clearAnimalProducts();
        refreshInventoryUI();
    }

    public void handleKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case M: toggleDebugMode(); break;
            case B: toggleBuildMode(); break;
            case X: toggleDestructionMode(); break;
            default: break;
        }
    }

    private void showPopup(Parent root, String title) {
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void handleModePlots() {
        setStructureToBuild(StructureType.PLOT);
    }

    @FXML
    public void handleModeEnclosures() {
        setStructureToBuild(StructureType.ENCLOSURE);
    }


    public void toggleDebugMode() {
        GameService.getInstance().toggleDebug();
        if (GameService.getInstance().isDebugActive()) wallet.addMoney(1000);
        updateStatusLabel();
        refreshInventoryUI();
    }

    public void refreshInventoryUI() { if (sidebarController != null) sidebarController.updateInventoryDisplay(); }
    public void updateWeatherUI(String text) { if (sidebarController != null) sidebarController.updateWeatherDisplay(text); }

    @FXML public void handleExit() { System.exit(0); }

    // Getters
    public boolean isBuildModeActive() { return buildModeActive; }
    public boolean isDestructionModeActive() { return destructionModeActive; }
    public StructureType getActiveStructureType() { return activeStructureType; }
    public void setStructureToBuild(StructureType type) { this.activeStructureType = type; updateStatusLabel(); }
}