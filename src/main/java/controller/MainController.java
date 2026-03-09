package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import main.java.model.*;
import main.java.service.GameService;
import java.io.IOException;

public class MainController {

    /**
     * DÉFINITION : L'énumération des types de structures.
     */
    public enum StructureType { PLOT, ENCLOSURE }

    @FXML private ScrollPane mapScroll;
    @FXML private Label statusLabel;

    @FXML private SidebarController sidebarController;
    @FXML private MapController mapController;

    private final Wallet wallet = new Wallet();
    private final Inventory inventory = new Inventory();

    private boolean buildModeActive = false;
    private StructureType activeStructureType = StructureType.PLOT; // Par défaut : Champs

    @FXML
    public void initialize() {
        // Synchronisation des services
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

    // --- GESTION DE LA CONSTRUCTION (Utilisé par MapController) ---

    public void setStructureToBuild(StructureType type) {
        this.activeStructureType = type;
        if (statusLabel != null) {
            statusLabel.setText("Construction : " + (type == StructureType.PLOT ? "CHAMPS" : "ENCLOS"));
        }
    }

    /**
     * Permet au MapController de savoir quoi construire.
     */
    public StructureType getActiveStructureType() {
        return activeStructureType;
    }

    public void toggleBuildMode() {
        this.buildModeActive = !this.buildModeActive;
        if (statusLabel != null) {
            statusLabel.setText(buildModeActive ? "MODE CONSTRUCTION ACTIF" : "MODE INTERACTION");
        }
    }

    public boolean isBuildModeActive() {
        return buildModeActive;
    }

    // --- ACTIONS CLAVIER ---

    @FXML
    public void handleKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case M: toggleDebugMode(); break;
            case B: toggleBuildMode(); break;
            default: break;
        }
    }

    // --- LOGIQUE DE VENTE ---

    public void sellCrops(CropType type, int qty) {
        if (type == null) return;
        int stock = inventory.getProducts().getOrDefault(type, 0);
        int amount = (qty == -1) ? stock : Math.min(qty, stock);
        if (amount > 0 && inventory.useProduct(type, amount)) {
            wallet.addMoney((int)(type.getBuyPrice() * 1.5) * amount);
            refreshInventoryUI();
        }
    }

    /**
     * Vend tous les produits animaux (Lait, Oeufs, Laine) présents dans l'inventaire.
     */
    public void sellAnimalProducts() {
        if (inventory == null || wallet == null) return;

        // On parcourt la map des produits récoltés
        inventory.getAnimalProducts().forEach((productName, qty) -> {
            if (qty > 0) {
                // On identifie le prix de vente en cherchant l'AnimalType correspondant au produit
                for (AnimalType type : AnimalType.values()) {
                    if (type.getProduct().equalsIgnoreCase(productName)) {
                        wallet.addMoney(type.getProductValue() * qty);
                        break;
                    }
                }
            }
        });

        // Une fois vendu, on vide le stock de produits animaux
        inventory.clearAnimalProducts();
        refreshInventoryUI();
    }

    // --- AUTRES MÉTHODES ---

    public void toggleDebugMode() {
        GameService.getInstance().toggleDebug();
        boolean isActive = GameService.getInstance().isDebugActive();

        if (statusLabel != null) {
            statusLabel.setText(isActive ? "DEBUG : ACTIF (+1000€)" : "DEBUG : DÉSACTIVÉ");
        }

        if (isActive) {
            wallet.addMoney(1000);
        }
        refreshInventoryUI();
    }

    public void openShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShopView.fxml"));
            Parent root = loader.load();
            ShopController shopCtrl = loader.getController();
            shopCtrl.setData(wallet, inventory, this);
            Stage stage = new Stage();
            stage.setTitle("Boutique");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void refreshInventoryUI() {
        if (sidebarController != null) sidebarController.updateInventoryDisplay();
    }

    public void updateWeatherUI(String w) {
        if (sidebarController != null) sidebarController.updateWeatherDisplay(w);
    }

    @FXML
    public void handleExit(ActionEvent event) { System.exit(0); }
}