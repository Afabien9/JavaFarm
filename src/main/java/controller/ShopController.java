package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.*;
import main.java.service.GameService;

/**
 * Contrôleur de la boutique.
 * Gère l'interface d'achat et délègue la logique au GameService.
 */
public class ShopController {
    @FXML private ListView<Object> shopListView;
    @FXML private Label shopWalletLabel;

    private MainController mainCtrl;

    /**
     * Initialise les données et lie l'affichage de l'argent.
     */
    public void setData(Wallet wallet, Inventory inventory, MainController mainCtrl) {
        this.mainCtrl = mainCtrl;

        if (shopWalletLabel != null && wallet != null) {
            shopWalletLabel.textProperty().bind(wallet.moneyProperty().asString("Mon argent: %d €"));
        }

        // Remplissage de la liste avec les graines et les animaux
        shopListView.getItems().clear();
        shopListView.getItems().addAll((Object[]) CropType.values());
        shopListView.getItems().addAll((Object[]) AnimalType.values());

        // Personnalisation de l'affichage des lignes
        shopListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item instanceof CropType) {
                    CropType c = (CropType) item;
                    setText("[GRAINE] " + c.getName() + " - " + c.getBuyPrice() + " €");
                } else if (item instanceof AnimalType) {
                    AnimalType a = (AnimalType) item;
                    setText("[ANIMAL] " + a.getName() + " - " + a.getBuyPrice() + " €");
                }
            }
        });
    }

    /**
     * Gère l'achat de l'élément sélectionné.
     */
    @FXML
    public void handleBuy(ActionEvent actionEvent) {
        processPurchase();
    }

    /**
     * MÉTHODE AJOUTÉE : Pour correspondre à certaines versions du FXML
     * qui utilisent onAction="#handleBuyPlot".
     */
    @FXML
    public void handleBuyPlot(ActionEvent actionEvent) {
        processPurchase();
    }

    /**
     * Logique commune d'achat pour éviter la duplication de code.
     */
    private void processPurchase() {
        Object selected = shopListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Délégation de l'achat au service centralisé
        boolean success = GameService.getInstance().buy(selected);

        if (success && mainCtrl != null) {
            mainCtrl.refreshInventoryUI();
        }
    }
}