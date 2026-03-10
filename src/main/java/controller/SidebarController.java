package main.java.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.*;
import main.java.service.SelectionService;

/**
 * Contrôleur de la barre latérale.
 * Gère l'affichage de l'inventaire (graines, animaux à placer, récoltes et produits animaux).
 */
public class SidebarController {

    @FXML private Label walletLabel;
    @FXML private Label weatherLabel;
    @FXML private ListView<String> inventoryListView;
    @FXML private ToggleButton buildToggle;

    private MainController mainCtrl;
    private Wallet wallet;
    private Inventory inventory;

    @FXML
    public void initialize() {
        if (inventoryListView != null) {
            inventoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    handleSelection(newVal);
                }
            });
        }
    }

    public void setupData(Wallet w, Inventory i) {
        this.wallet = w;
        this.inventory = i;
        updateInventoryDisplay();
    }

    private void handleSelection(String text) {
        if (text.startsWith("-")) return;

        String name = text.split(" ")[0];

        // Sélection de graines
        for (CropType type : CropType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                SelectionService.getInstance().setSelectedCrop(type);
                SelectionService.getInstance().setSelectedAnimal(null);
                return;
            }
        }

        // Sélection d'animaux en stock
        for (AnimalType type : AnimalType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                SelectionService.getInstance().setSelectedAnimal(type);
                SelectionService.getInstance().setSelectedCrop(null);
                return;
            }
        }
    }

    /**
     * Rafraîchit la liste de l'inventaire avec toutes les catégories.
     */
    public void updateInventoryDisplay() {
        if (inventory == null) return;

        ObservableList<String> items = FXCollections.observableArrayList();

        // 1. GRAINES
        items.add("--- GRAINES ---");
        inventory.getSeeds().forEach((t, q) -> { if(q > 0) items.add(t.getName() + " (Stock: " + q + ")"); });

        // 2. ANIMAUX À PLACER
        items.add("--- ANIMAUX À PLACER ---");
        inventory.getAnimals().forEach((t, q) -> { if(q > 0) items.add(t.getName() + " (Stock: " + q + ")"); });

        // 3. RÉCOLTES VÉGÉTALES
        items.add("--- RÉCOLTES (CHAMPS) ---");
        inventory.getProducts().forEach((t, q) -> { if(q > 0) items.add(t.getName() + " (Prêt: " + q + ")"); });

        // 4. PRODUITS ANIMAUX (Nouveau !)
        items.add("--- PRODUITS ANIMAUX ---");
        if (inventory.getAnimalProducts() != null) {
            inventory.getAnimalProducts().forEach((productName, qty) -> {
                if (qty > 0) items.add(productName + " (Qte: " + qty + ")");
            });
        }

        if (inventoryListView != null) {
            inventoryListView.setItems(items);
        }

        if (walletLabel != null && wallet != null) {
            walletLabel.setText("Argent: " + wallet.getMoney() + " €");
        }
    }

    public void updateWeatherDisplay(String text) {
        if (weatherLabel != null) {
            weatherLabel.setText("MÉTÉO : " + text.toUpperCase());
        }
    }

    @FXML
    private void handleModePlots() {
        if (mainCtrl != null) mainCtrl.setStructureToBuild(MainController.StructureType.PLOT);
    }

    @FXML
    private void handleModeEnclosures() {
        if (mainCtrl != null) mainCtrl.setStructureToBuild(MainController.StructureType.ENCLOSURE);
    }

    @FXML
    private void handleToggleBuild() {
        if (mainCtrl != null) mainCtrl.toggleBuildMode();
    }

    @FXML
    private void handleOpenShop() {
        if (mainCtrl != null) mainCtrl.openShop();
    }

    @FXML
    private void handleOpenDebug() {
        if (mainCtrl != null) mainCtrl.toggleDebugMode();
    }
    @FXML
    private void handleOpenInventory() {
        mainCtrl.handleOpenInventory(); // Déléguez au MainController qui gère les fenêtres
    }

    /**
     * Vend l'intégralité de l'inventaire.
     */
    @FXML
    private void sellAll() {
        if (mainCtrl != null) {
            // Vente des plantes
            for (CropType type : CropType.values()) {
                mainCtrl.sellCrops(type, -1);
            }
            // Vente des produits animaux
            mainCtrl.sellAnimalProducts();
        }

    }

    public void setMainController(MainController m) {
        this.mainCtrl = m;
    }
}