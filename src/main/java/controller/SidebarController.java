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
    @FXML private Label levelLabel;
    @FXML private Label xpLabel;
    @FXML private ProgressBar xpBar;
    @FXML private Button debugXpBtn;
    @FXML private ListView<String> inventoryListView;

    private MainController mainCtrl;
    private Wallet wallet;
    private Inventory inventory;

    @FXML
    public void initialize() {

        if (inventoryListView != null) {
            inventoryListView.getSelectionModel().selectedItemProperty().addListener((obs,
                                                                                      oldVal, newVal) -> {
                if (newVal != null) handleSelection(newVal);
            });
        }


        if (debugXpBtn != null) debugXpBtn.setVisible(false);
    }

    public void setupData(Wallet w, Inventory i) {
        this.wallet = w;
        this.inventory = i;


        if (wallet != null && walletLabel != null) {
            wallet.moneyProperty().addListener((obs, oldVal, newVal) -> {
                Platform.runLater(() -> walletLabel.setText(newVal.intValue() + " €"));
            });
            walletLabel.setText(wallet.getMoney() + " €");
        }


        LevelService ls = LevelService.getInstance();
        if (levelLabel != null) {
            ls.currentLevelProperty().addListener((obs, old, newVal) -> {
                Platform.runLater(() -> {
                    levelLabel.setText(newVal.toString());
                    if (mainCtrl != null) mainCtrl.showLevelUpNotification(newVal.intValue());
                });
            });
            levelLabel.setText(String.valueOf(ls.getCurrentLevel()));
        }


        ls.currentXPProperty().addListener((obs, old, newVal) -> updateXPUI());
        ls.xpToNextLevelProperty().addListener((obs, old, newVal) -> updateXPUI());


        GameService.getInstance().debugModeProperty().addListener((obs, old, isDebug) -> {
            Platform.runLater(() -> {
                if (debugXpBtn != null) {
                    debugXpBtn.setVisible(isDebug);
                    debugXpBtn.setManaged(isDebug);
                }
            });
        });

        updateXPUI();
    }

    private void updateXPUI() {
        LevelService ls = LevelService.getInstance();
        int xp = ls.getCurrentXP();
        int max = ls.getXpToNextLevel();
        double progress = (double) xp / max;

        Platform.runLater(() -> {
            if (xpBar != null) xpBar.setProgress(progress);
            if (xpLabel != null) xpLabel.setText(xp + " / " + max + " XP");
        });
    }

    @FXML
    private void handleDebugAddXP() {
        LevelService.getInstance().addXP(50);
    }

    @FXML private void handleSellAll() {
        if (mainCtrl != null) mainCtrl.sellAll();
    }

    @FXML private void handleOpenShop() { if (mainCtrl != null) mainCtrl.openShop(); }
    @FXML private void handleOpenInventory() { if (mainCtrl != null) mainCtrl.handleOpenInventory(); }
    @FXML private void handleOpenUpgrades() { if (mainCtrl != null) mainCtrl.openUpgradeMenu(); }
    @FXML
    private void handleOpenHelp() {
        if (mainCtrl != null) {
            mainCtrl.openHelpMenu();
        }
    }
    @FXML
    private void handleOpenMarket() {
        if (mainCtrl != null) mainCtrl.openMarket();
    }

    public void setMainController(MainController m) { this.mainCtrl = m; }

    private void handleSelection(String text) {
        if (text == null || text.startsWith("-")) return;


        String cleanText = text.replaceAll("[^a-zA-Záàâäãåçéèêëíìîïñóòôöõúùûüýÿ ]", "").trim();

        String name = cleanText.split(" ")[0];


        for (CropType type : CropType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                SelectionService.getInstance().setSelectedCrop(type);
                SelectionService.getInstance().setSelectedAnimal(null);
                System.out.println("[SELECTION] Graine choisie : " + type.getName());
                return;
            }
        }


        for (AnimalType type : AnimalType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                SelectionService.getInstance().setSelectedAnimal(type);
                SelectionService.getInstance().setSelectedCrop(null);
                System.out.println("[SELECTION] Animal choisi : " + type.getName());
                return;
            }
        }
    }


    public void updateInventoryDisplay() {
        if (inventory == null || inventoryListView == null) return;

        ObservableList<String> items = FXCollections.observableArrayList();


        items.add("--- SEMENCES ---");
        inventory.getSeeds().forEach((t, q) -> {
            if(q > 0) items.add(getCropEmoji(t.getName()) + " " + t.getName() + " x" + q);
        });


        items.add("--- ANIMAUX ---");
        inventory.getAnimals().forEach((t, q) -> {
            if(q > 0) items.add(getAnimalEmoji(t.getName()) + " " + t.getName() + " x" + q);
        });


        items.add("--- STOCK RÉCOLTES ---");
        inventory.getProducts().forEach((t, q) -> {
            if(q > 0) items.add(t.getName() + " (" + q + ")");
        });


        if (inventory.getAnimalProducts() != null && !inventory.getAnimalProducts().isEmpty()) {
            items.add("--- PRODUITS ANIMAUX ---");
            inventory.getAnimalProducts().forEach((name, qty) -> {
                if(qty > 0) items.add(name + " (x" + qty + ")");
            });
        }


        Platform.runLater(() -> inventoryListView.setItems(items));
    }


    public void updateWeatherDisplay(String text) {
        if (weatherLabel != null) {
            Platform.runLater(() -> weatherLabel.setText("MÉTÉO : " + text.toUpperCase()));
        }
    }



    private String getCropEmoji(String name) {
        String n = name.toLowerCase();
        if (n.contains("blé")) return "🌾";
        if (n.contains("maïs")) return "🌽";
        if (n.contains("carotte")) return "🥕";
        if (n.contains("tomate")) return "🍅";
        return "🌱";
    }

    private String getAnimalEmoji(String name) {
        String n = name.toLowerCase();
        if (n.contains("poule")) return "🐔";
        if (n.contains("vache")) return "🐄";
        if (n.contains("mouton")) return "🐑";
        if (n.contains("cochon")) return "🐷";
        return "🐾";
    }
}