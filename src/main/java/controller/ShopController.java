package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.*;
import main.java.service.GameService;

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
        seedsListView.setCellFactory(lv -> new ListCell<CropType>() {
            @Override
            protected void updateItem(CropType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getCropEmoji(item.getName()) + " " + item.getName() + " (" + item.getBuyPrice() + " €)");
                }
            }
        });
    }

    private void setupAnimalsList() {
        if (animalsListView == null) return;
        animalsListView.getItems().setAll(AnimalType.values());
        animalsListView.setCellFactory(lv -> new ListCell<AnimalType>() {
            @Override
            protected void updateItem(AnimalType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getAnimalEmoji(item.getName()) + " " + item.getName() + " (" + item.getBuyPrice() + " €)");
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
        // On vérifie d'abord l'onglet des graines, puis celui des animaux
        Object selected = seedsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            selected = animalsListView.getSelectionModel().getSelectedItem();
        }

        if (selected != null && GameService.getInstance().buy(selected)) {
            mainCtrl.refreshInventoryUI();
        }
    }

    @FXML
    public void handleBuyPlot() {
        // Logique pour l'onglet Terrain
        System.out.println("Achat terrain demandé via l'interface");
    }

    @FXML
    public void handleBuyBonus() {
        // Méthode prévue pour ton futur onglet Bonus
        System.out.println("Achat Bonus (Bientôt disponible)");
    }

    @FXML
    public void handleCloseShop() {
        if (shopWalletLabel != null && shopWalletLabel.getScene() != null) {
            shopWalletLabel.getScene().getWindow().hide();
        }
    }
}