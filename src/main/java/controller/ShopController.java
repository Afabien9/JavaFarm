package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.*;

/**
 * Contrôleur pour la vue de la boutique.
 * Gère l'achat de graines et de terrains.
 */
public class ShopController {
    @FXML private ListView<CropType> shopListView;
    @FXML private Label shopWalletLabel;

    private Wallet wallet;
    private Inventory inventory;
    private MainController mainCtrl;

    /**
     * Initialise les données de la boutique.
     */
    public void setData(Wallet wallet, Inventory inventory, MainController mainCtrl) {
        this.wallet = wallet;
        this.inventory = inventory;
        this.mainCtrl = mainCtrl;

        // Liaison de l'argent affiché au modèle Wallet
        if (shopWalletLabel != null && wallet != null) {
            shopWalletLabel.textProperty().bind(wallet.moneyProperty().asString("Mon argent: %d €"));
        }

        // Remplissage de la liste avec les types de cultures
        if (shopListView != null) {
            shopListView.getItems().setAll(CropType.values());

            // Personnalisation de l'affichage (Nom + Prix)
            shopListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(CropType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " - " + item.getBuyPrice() + " €");
                    }
                }
            });
        }
    }

    /**
     * Gère l'achat de graines pour la culture sélectionnée.
     */
    @FXML
    private void handleBuy(ActionEvent event) {
        CropType selected = shopListView.getSelectionModel().getSelectedItem();
        if (selected != null && wallet != null) {
            if (wallet.spendMoney(selected.getBuyPrice())) {
                // On ajoute des GRAINES (seeds) pour pouvoir planter
                inventory.addSeed(selected, 1);
                if (mainCtrl != null) {
                    mainCtrl.refreshInventoryUI();
                }
                System.out.println("ACHAT : 1 graine de " + selected.getName() + " achetée.");
            } else {
                System.out.println("SOLDE : Pas assez d'argent pour acheter du " + selected.getName());
            }
        }
    }

    /**
     * Gère l'achat d'une extension de terrain.
     * Correction de l'erreur 'Error resolving onAction' en ajoutant ActionEvent.
     */
    @FXML
    private void handleBuyPlot(ActionEvent event) {
        int price = 500;
        if (wallet != null && wallet.spendMoney(price)) {
            System.out.println("BOUTIQUE : Terrain acheté ! Vous pouvez maintenant le placer.");
            // Logique supplémentaire pour débloquer le placement de terrain si nécessaire
        } else {
            System.out.println("BOUTIQUE : Pas assez d'argent pour acheter un terrain (500€).");
        }
    }
}