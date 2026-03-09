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

    @FXML private ScrollPane mapScroll;
    @FXML private Label statusLabel;

    // ATTENTION : Ces noms doivent correspondre aux fx:id du FXML + "Controller"
    @FXML private SidebarController sidebarController;
    @FXML private MapController mapController;

    private final Wallet wallet = new Wallet();
    private final Inventory inventory = new Inventory();
    private boolean buildModeActive = false;

    @FXML
    public void initialize() {
        System.out.println("LOG : Initialisation du MainController...");

        // On lie l'inventaire et le portefeuille au Service global
        GameService.getInstance().setInventory(this.inventory);
        GameService.getInstance().setWallet(this.wallet);

        // Injection des données dans les sous-contrôleurs
        if (sidebarController != null) {
            sidebarController.setMainController(this);
            sidebarController.setupData(wallet, inventory);
        }
        if (mapController != null) {
            mapController.setMainController(this);
            mapController.setupData(wallet, inventory);
        }

        // On donne le focus au scrollPane pour capter le clavier immédiatement
        javafx.application.Platform.runLater(() -> {
            if (mapScroll != null) mapScroll.requestFocus();
        });
    }

    /**
     * Gère les entrées clavier (Z,S,Q,D pour le scroll, M pour le Debug).
     */
    @FXML
    public void handleKeyPress(KeyEvent e) {
        if (mapScroll == null) return;
        double step = 0.1;
        switch (e.getCode()) {
            case M:
                toggleDebugMode();
                break;
            case Z: case UP:    mapScroll.setVvalue(mapScroll.getVvalue() - step); break;
            case S: case DOWN:  mapScroll.setVvalue(mapScroll.getVvalue() + step); break;
            case Q: case LEFT:  mapScroll.setHvalue(mapScroll.getHvalue() - step); break;
            case D: case RIGHT: mapScroll.setHvalue(mapScroll.getHvalue() + step); break;
            default: break;
        }
    }

    /**
     * Met à jour l'affichage de la météo dans la sidebar.
     * CETTE MÉTHODE ÉTAIT MANQUANTE ET CAUSAIT L'ERREUR.
     */
    public void updateWeatherUI(String weatherName) {
        if (sidebarController != null) {
            sidebarController.updateWeatherDisplay(weatherName);
        }
    }

    /**
     * Alterne le mode de croissance rapide (1s) via le GameService.
     */
    @FXML
    public void toggleDebugMode() {
        GameService.getInstance().toggleDebug();
        boolean isActive = GameService.getInstance().isDebugActive();

        if (statusLabel != null) {
            statusLabel.setText(isActive ? "MODE DEBUG : ACTIF (Croissance 1s)" : "MODE DEBUG : DÉSACTIVÉ");
        }

        if (isActive) {
            wallet.addMoney(500); // Petit bonus de test
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
        } catch (IOException e) {
            System.err.println("Erreur boutique : " + e.getMessage());
        }
    }

    public void sellCrops(CropType type, int amount) {
        if (type == null) return;
        int stock = inventory.getProducts().getOrDefault(type, 0);
        int qty = (amount == -1) ? stock : Math.min(amount, stock);

        if (qty > 0 && inventory.useProduct(type, qty)) {
            int gain = (int) (qty * type.getBuyPrice() * 1.5);
            wallet.addMoney(gain);
            refreshInventoryUI();
        }
    }

    public void toggleBuildMode() {
        this.buildModeActive = !this.buildModeActive;
        System.out.println("Mode construction : " + (buildModeActive ? "ON" : "OFF"));
    }

    public boolean isBuildModeActive() {
        return buildModeActive;
    }

    public void refreshInventoryUI() {
        if (sidebarController != null) sidebarController.updateInventoryDisplay();
    }

    /**
     * Ferme l'application proprement.
     */
    @FXML
    public void handleExit(ActionEvent event) {
        System.out.println("Fermeture du jeu...");
        System.exit(0);
    }
}