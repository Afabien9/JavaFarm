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

        // Récupère le niveau actuel du joueur
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
                        setDisable(true); // Empêche la sélection
                        setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;"); // Apparence grisée
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

        // On pourrait aussi ajouter des niveaux aux animaux plus tard
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
        if (selected == null) {
            selected = animalsListView.getSelectionModel().getSelectedItem();
        }

        if (selected != null) {
            // Sécurité supplémentaire : On vérifie le niveau avant d'appeler le GameService
            if (selected instanceof CropType) {
                CropType crop = (CropType) selected;
                if (LevelService.getInstance().getCurrentLevel() < crop.getMinLevel()) {
                    System.out.println("Niveau insuffisant !");
                    return;
                }
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