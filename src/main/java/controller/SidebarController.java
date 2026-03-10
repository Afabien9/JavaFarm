package main.java.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.*;
import main.java.service.GameService;
import main.java.service.LevelService;
import main.java.service.SelectionService;

public class SidebarController {

    @FXML private Label walletLabel;
    @FXML private Label weatherLabel;
    @FXML private ListView<String> inventoryListView;
    @FXML private ToggleButton buildToggle;
    @FXML private Label levelLabel;
    @FXML private Label xpLabel;
    @FXML private ProgressBar xpBar;
    @FXML private Button debugXpBtn;

    private MainController mainCtrl;
    private Wallet wallet;
    private Inventory inventory;

    @FXML
    public void initialize() {
        if (inventoryListView != null) {
            // Permet de sélectionner un animal ou une graine en cliquant dans la liste
            inventoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) handleSelection(newVal);
            });
        }
    }

    public void setupData(Wallet w, Inventory i) {
        this.wallet = w;
        this.inventory = i;

        // Liaison Argent
        if (wallet != null) {
            wallet.moneyProperty().addListener((obs, oldVal, newVal) -> {
                Platform.runLater(() -> walletLabel.setText(newVal.intValue() + " €"));
            });
            walletLabel.setText(wallet.getMoney() + " €");
        }

        // --- Liaison Système de Niveau ---
        LevelService ls = LevelService.getInstance();
        ls.currentLevelProperty().addListener((obs, old, newVal) -> {
            Platform.runLater(() -> {
                levelLabel.setText(newVal.toString());
                if (mainCtrl != null) mainCtrl.showLevelUpNotification(newVal.intValue());
            });
        });
        levelLabel.setText(String.valueOf(ls.getCurrentLevel()));

        // Liaison XP
        ls.currentXPProperty().addListener((obs, old, newVal) -> updateXPUI());
        ls.xpToNextLevelProperty().addListener((obs, old, newVal) -> updateXPUI());

        // Liaison Mode Debug
        GameService.getInstance().debugModeProperty().addListener((obs, old, isDebug) -> {
            Platform.runLater(() -> {
                if (debugXpBtn != null) {
                    debugXpBtn.setVisible(isDebug);
                    debugXpBtn.setManaged(isDebug);
                }
            });
        });

        updateXPUI();
        updateInventoryDisplay();
    }

    private void updateXPUI() {
        Platform.runLater(() -> {
            LevelService ls = LevelService.getInstance();
            int xp = ls.getCurrentXP();
            int max = ls.getXpToNextLevel();
            double progress = (double) xp / max;
            if (xpBar != null) xpBar.setProgress(progress);
            if (xpLabel != null) xpLabel.setText(xp + " / " + max + " XP");
        });
    }

    @FXML
    private void handleToggleDestruction() {
        if (mainCtrl != null) mainCtrl.toggleDestructionMode();
    }

    @FXML
    private void handleDebugAddXP() {
        LevelService.getInstance().addXP(50);
        System.out.println("[DEBUG] XP ajoutée manuellement (+50)");
    }

    private void handleSelection(String text) {
        if (text == null || text.startsWith("-")) return;

        // Nettoyage de la chaîne pour récupérer le nom (ex: "🌾 Blé x5" -> "Blé")
        String cleanText = text.replaceAll("[^a-zA-Záàâäãåçéèêëíìîïñóòôöõúùûüýÿ ]", "").trim();
        String name = cleanText.split(" ")[0];

        for (CropType type : CropType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                SelectionService.getInstance().setSelectedCrop(type);
                SelectionService.getInstance().setSelectedAnimal(null);
                return;
            }
        }
        for (AnimalType type : AnimalType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                SelectionService.getInstance().setSelectedAnimal(type);
                SelectionService.getInstance().setSelectedCrop(null);
                return;
            }
        }
    }

    public void updateInventoryDisplay() {
        if (inventory == null) return;
        ObservableList<String> items = FXCollections.observableArrayList();

        items.add("--- SEMENCES ---");
        inventory.getSeeds().forEach((t, q) -> { if(q > 0) items.add(getCropEmoji(t.getName()) + " " + t.getName() + " x" + q); });

        items.add("--- ANIMAUX ---");
        inventory.getAnimals().forEach((t, q) -> { if(q > 0) items.add(getAnimalEmoji(t.getName()) + " " + t.getName() + " x" + q); });

        items.add("--- STOCK RÉCOLTES ---");
        inventory.getProducts().forEach((t, q) -> { if(q > 0) items.add(t.getName() + " (" + q + ")"); });

        if (inventory.getAnimalProducts() != null && !inventory.getAnimalProducts().isEmpty()) {
            items.add("--- PRODUITS ANIMAUX ---");
            inventory.getAnimalProducts().forEach((name, qty) -> items.add(name + " (x" + qty + ")"));
        }

        Platform.runLater(() -> {
            if (inventoryListView != null) inventoryListView.setItems(items);
        });
    }

    private String getCropEmoji(String name) {
        if (name.toLowerCase().contains("blé")) return "🌾";
        if (name.toLowerCase().contains("maïs")) return "🌽";
        if (name.toLowerCase().contains("carotte")) return "🥕";
        if (name.toLowerCase().contains("tomate")) return "🍅";
        return "🌱";
    }

    private String getAnimalEmoji(String name) {
        if (name.toLowerCase().contains("poule")) return "🐔";
        if (name.toLowerCase().contains("vache")) return "🐄";
        if (name.toLowerCase().contains("mouton")) return "🐑";
        return "🐾";
    }

    public void updateWeatherDisplay(String text) {
        if (weatherLabel != null) {
            Platform.runLater(() -> weatherLabel.setText("MÉTÉO : " + text.toUpperCase()));
        }
    }

    @FXML private void handleOpenUpgrades() { if (mainCtrl != null) mainCtrl.openUpgradeMenu(); }
    @FXML private void handleSellAll() { if (mainCtrl != null) { mainCtrl.sellAll(); updateInventoryDisplay(); } }
    @FXML private void handleModePlots() { if (mainCtrl != null) mainCtrl.setStructureToBuild(MainController.StructureType.PLOT); }
    @FXML private void handleModeEnclosures() { if (mainCtrl != null) mainCtrl.setStructureToBuild(MainController.StructureType.ENCLOSURE); }
    @FXML private void handleToggleBuild() { if (mainCtrl != null) mainCtrl.toggleBuildMode(); }
    @FXML private void handleOpenShop() { if (mainCtrl != null) mainCtrl.openShop(); }
    @FXML private void handleOpenInventory() { if (mainCtrl != null) mainCtrl.handleOpenInventory(); }

    public void setMainController(MainController m) { this.mainCtrl = m; }
}