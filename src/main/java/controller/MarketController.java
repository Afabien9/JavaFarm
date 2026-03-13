package main.java.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.java.model.*;
import main.java.service.GameService;

public class MarketController {

    @FXML private VBox marketContainer;
    @FXML private Label marketWalletLabel;

    private Wallet wallet;
    private Inventory inventory;
    private MainController mainController;

    public void setData(Wallet wallet, Inventory inventory, MainController mainController) {
        this.wallet = wallet;
        this.inventory = inventory;
        this.mainController = mainController;

        if (marketWalletLabel != null) {
            marketWalletLabel.textProperty().bind(wallet.moneyProperty().asString("%d €"));
        }
        refreshMarket();
    }

    private void refreshMarket() {
        marketContainer.getChildren().clear();


        inventory.getProducts().forEach((type, qty) -> {
            if (qty > 0) {

                int currentPrice = type.getSellPrice();
                marketContainer.getChildren().add(createMarketRow(type.getName(), qty, currentPrice, type));
            }
        });


        inventory.getAnimalProducts().forEach((name, qty) -> {
            if (qty > 0) marketContainer.getChildren().add(createMarketRow(name, qty, 50, name));
        });
    }

    private HBox createMarketRow(String name, int stock, int unitPrice, Object itemType) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #34495e; -fx-padding: 10; -fx-background-radius: 8;");

        Label nameLabel = new Label(name + " (Stock: " + stock + ")");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 150;");

        TextField qtyInput = new TextField("1");
        qtyInput.setPrefWidth(50);
        qtyInput.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-border-color: #7f8c8d;");

        // Bouton avec le prix actuel
        Button sellBtn = new Button("VENDRE (" + unitPrice + "€/u)");
        sellBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        sellBtn.setOnAction(e -> {
            try {
                int amountToSell = Integer.parseInt(qtyInput.getText());
                if (amountToSell <= 0) return;

                int actualSold = 0;
                int totalGain = 0;

                if (itemType instanceof CropType) {
                    CropType type = (CropType) itemType;
                    if (amountToSell <= stock && inventory.useProduct(type, amountToSell)) {
                        actualSold = amountToSell;
                        totalGain = actualSold * type.getSellPrice();


                        type.increaseValue();
                        System.out.println("[MARCHÉ] La valeur de " + type.getName() + " a augmenté !");
                    }
                } else if (itemType instanceof String) { // Produit animal
                    if (amountToSell <= stock) {
                        inventory.getAnimalProducts().put((String)itemType, stock - amountToSell);
                        actualSold = amountToSell;
                        totalGain = actualSold * unitPrice;
                    }
                }

                if (actualSold > 0) {
                    wallet.addMoney(totalGain);
                    refreshMarket();
                    if (mainController != null) mainController.refreshInventoryUI();
                }
            } catch (NumberFormatException ex) {
                qtyInput.setText("1");
            }
        });

        row.getChildren().addAll(nameLabel, spacer(), qtyInput, sellBtn);
        return row;
    }


    private Pane spacer() {
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @FXML private void handleClose() {
        ((Stage) marketContainer.getScene().getWindow()).close();
    }
}