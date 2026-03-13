package main.java.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import java.io.IOException;
import java.util.Optional;

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

    public Wallet getWallet() {
        return this.wallet;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @FXML
    public void handleResetGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Total");
        alert.setHeaderText("Réinitialisation de la partie");
        alert.setContentText("Voulez-vous supprimer votre sauvegarde et recommencer ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SaveManager.deleteSave();
            System.exit(0);
        }
    }

    public void handleAnimalInteraction(Enclosure enclosure) {
        Animal animal = enclosure.getCurrentAnimal();
        if (animal == null) return;

        if (animal.isHungry()) {
            feedAnimal(enclosure);
        } else if (animal.isReadyToProduce()) {
            harvestAnimal(enclosure);
        }
    }

    private void feedAnimal(Enclosure enclosure) {
        Animal animal = enclosure.getCurrentAnimal();
        if (animal == null) return;

        CropType foodNeeded;
        String animalName = animal.getName().toLowerCase();

        if (animalName.contains("vache")) foodNeeded = CropType.CABBAGE;
        else if (animalName.contains("mouton")) foodNeeded = CropType.CORN;
        else foodNeeded = CropType.WHEAT;

        int stock = inventory.getProducts().getOrDefault(foodNeeded, 0);

        if (stock >= 1) {
            inventory.useProduct(foodNeeded, 1);
            animal.feed();
            refreshInventoryUI();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Faim");
            alert.setHeaderText("Pas de nourriture !");
            alert.setContentText("Il vous faut 1 unité de " + foodNeeded.getName() + " pour nourrir cet animal.");
            alert.showAndWait();
        }
    }

    private void harvestAnimal(Enclosure enclosure) {
        Animal animal = enclosure.getCurrentAnimal();
        if (animal == null) return;

        String product = animal.getProducedProductName();
        inventory.addAnimalProduct(product, 1);
        animal.resetProduction();
        refreshInventoryUI();
    }

    @FXML
    public void handleSellAll() {
        sellAll();
    }

    public void sellAll() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Vente Totale");
        alert.setHeaderText("Confirmation de vente");
        alert.setContentText("Voulez-vous vendre TOUTE votre production actuelle ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            for (CropType type : CropType.values()) sellCrops(type, -1);
            sellAnimalProducts();
            refreshInventoryUI();
        }
    }

    public void sellCrops(CropType type, int qty) {
        if (type == null) return;
        int stock = inventory.getProducts().getOrDefault(type, 0);
        int amount = (qty == -1) ? stock : Math.min(qty, stock);
        if (amount > 0 && inventory.useProduct(type, amount)) {
            wallet.addMoney(type.getSellPrice() * amount);
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
            boolean isPlot = (activeStructureType == StructureType.PLOT);
            int current = isPlot ? mapController.getCurrentPlotCount() : mapController.getCurrentEnclosureCount();
            int max = isPlot ? LevelService.getInstance().getMaxPlots() : LevelService.getInstance().getMaxEnclosures();
            int nextPrice = isPlot ? GameService.getInstance().getNextPlotPrice(current) : GameService.getInstance().getNextEnclosurePrice(current);
            String name = isPlot ? "CHAMPS" : "ENCLOS";

            statusLabel.setText(String.format("CONSTRUCTION %s : %d/%d (Prix: %d €)", name, current, max, nextPrice));
            statusLabel.setStyle("-fx-text-fill: " + (wallet.getMoney() < nextPrice ? "#f39c12" : "#2ecc71") + "; -fx-font-weight: bold;");
            modeOverlay.setStyle("-fx-border-width: 15; -fx-border-color: #7f8c8d; -fx-border-style: solid;");
        }
        else {
            statusLabel.setText("MODE INTERACTION");
            statusLabel.setStyle("-fx-text-fill: white;");
            modeOverlay.setStyle("-fx-border-color: transparent;");
        }
        if (buildToolbar != null) buildToolbar.setVisible(buildModeActive);
    }

    public void openMarket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MarketView.fxml"));
            Parent root = loader.load();
            ((MarketController)loader.getController()).setData(wallet, inventory, this);
            showPopup(root, "Marché");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void openShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShopView.fxml"));
            Parent root = loader.load();
            ((ShopController)loader.getController()).setData(wallet, inventory, this);
            showPopup(root, "Boutique");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void openUpgradeMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpgradeView.fxml"));
            Parent root = loader.load();
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

    @FXML public void handleModePlots() { setStructureToBuild(StructureType.PLOT); }
    @FXML public void handleModeEnclosures() { setStructureToBuild(StructureType.ENCLOSURE); }

    public void toggleDebugMode() {
        GameService.getInstance().toggleDebug();
        if (GameService.getInstance().isDebugActive()) wallet.addMoney(1000);
        updateStatusLabel();
        refreshInventoryUI();
    }

    public void openHelpMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelpView.fxml"));
            Parent root = loader.load();
            showPopup(root, "Aide et Règles");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public MapController getMapController() {
        return this.mapController;
    }

    public void refreshInventoryUI() { if (sidebarController != null) sidebarController.updateInventoryDisplay(); }
    public void updateWeatherUI(String text) { if (sidebarController != null) sidebarController.updateWeatherDisplay(text); }
    @FXML public void handleExit() { System.exit(0); }

    public boolean isBuildModeActive() { return buildModeActive; }
    public boolean isDestructionModeActive() { return destructionModeActive; }
    public StructureType getActiveStructureType() { return activeStructureType; }
    public void setStructureToBuild(StructureType type) { this.activeStructureType = type; updateStatusLabel(); }
}