package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.*;
import main.java.service.GameService;
import main.java.service.LevelService;

public class ShopController {
    @FXML private ListView<CropType> seedsListView;
    @FXML private ListView<AnimalType> animalsListView;
    @FXML private Label shopWalletLabel;

    private MainController mainCtrl;

    public void setData(Wallet wallet, Inventory inventory, MainController mainCtrl) {
        this.mainCtrl = mainCtrl;

        if (shopWalletLabel != null && wallet != null) {
            shopWalletLabel.textProperty().bind(wallet.moneyProperty().asString("Mon argent: %d €"));
        }

        setupSeedsList();
        setupAnimalsList();
    }

    private void setupSeedsList() {
        if (seedsListView == null) return;
        seedsListView.getItems().setAll(CropType.values());

        int currentLevel = LevelService.getInstance().getCurrentLevel();

        seedsListView.setCellFactory(lv -> new ListCell<CropType>() {
            @Override
            protected void updateItem(CropType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {
                    boolean isLocked = currentLevel < item.getMinLevel();

                    if (isLocked) {
                        setText("🔒 " + item.getName() + " [NIV. " + item.getMinLevel() + " REQUIS]");
                        setDisable(true);
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic; -fx-opacity: 0.7;");
                    } else {
                        setText(getCropEmoji(item.getName()) + " " + item.getName() + " (" + item.getBuyPrice() + " €)");
                        setDisable(false);
                        setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupAnimalsList() {
        if (animalsListView == null) return;
        animalsListView.getItems().setAll(AnimalType.values());

        int currentLevel = LevelService.getInstance().getCurrentLevel();

        animalsListView.setCellFactory(lv -> new ListCell<AnimalType>() {
            @Override
            protected void updateItem(AnimalType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {

                    boolean isLocked = currentLevel < item.getRequiredLevel();

                    if (isLocked) {
                        setText("🔒 " + item.getName() + " [NIV. " + item.getRequiredLevel() + " REQUIS]");
                        setDisable(true);
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic; -fx-opacity: 0.7;");
                    } else {
                        setText(getAnimalEmoji(item.getName()) + " " + item.getName() + " (" + item.getBuyPrice() + " €)");
                        setDisable(false);
                        setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private String getCropEmoji(String n) {
        String name = n.toLowerCase();
        if (name.contains("blé")) return "🌾";
        if (name.contains("maïs")) return "🌽";
        if (name.contains("carotte")) return "🥕";
        if (name.contains("tomate")) return "🍅";
        if (name.contains("chou")) return "🥬";
        return "🌱";
    }

    private String getAnimalEmoji(String n) {
        String name = n.toLowerCase();
        if (name.contains("poule")) return "🐔";
        if (name.contains("vache")) return "🐄";
        if (name.contains("mouton")) return "🐑";
        if (name.contains("cochon")) return "🐷";
        return "🐾";
    }

    @FXML
    public void handleBuy(ActionEvent event) {
        Object selected = seedsListView.getSelectionModel().getSelectedItem();
        boolean fromSeeds = true;

        if (selected == null) {
            selected = animalsListView.getSelectionModel().getSelectedItem();
            fromSeeds = false;
        }

        if (selected != null) {
            int currentLevel = LevelService.getInstance().getCurrentLevel();


            if (fromSeeds) {
                if (currentLevel < ((CropType) selected).getMinLevel()) return;
            } else {
                if (currentLevel < ((AnimalType) selected).getRequiredLevel()) return;
            }

            if (GameService.getInstance().buy(selected)) {
                mainCtrl.refreshInventoryUI();
            }
        }
    }

    @FXML public void handleBuyPlot() { System.out.println("Achat terrain demandé"); }
    @FXML public void handleBuyBonus() { System.out.println("Achat Bonus (Bientôt)"); }

    @FXML
    public void handleCloseShop() {
        if (shopWalletLabel != null && shopWalletLabel.getScene() != null) {
            shopWalletLabel.getScene().getWindow().hide();
        }
    }
}