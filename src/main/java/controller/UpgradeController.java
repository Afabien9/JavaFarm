package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.model.UpgradeType;
import main.java.model.Wallet;
import main.java.service.LevelService;
import main.java.service.UpgradeService;

public class UpgradeController {

    @FXML private VBox upgradeContainer;
    @FXML private Label walletLabel;

    private Wallet wallet;
    private MainController mainController;

    public void setData(Wallet wallet, MainController mainController) {
        this.wallet = wallet;
        this.mainController = mainController;
        updateUI();
    }

    private void updateUI() {
        upgradeContainer.getChildren().clear();
        walletLabel.setText("Argent disponible : " + wallet.getMoney() + "€");

        for (UpgradeType type : UpgradeType.values()) {
            upgradeContainer.getChildren().add(createUpgradeCard(type));
        }
    }

    private VBox createUpgradeCard(UpgradeType type) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: #34495e; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #7f8c8d; -fx-border-radius: 10;");

        Label nameLabel = new Label(type.getName());
        nameLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 16;");

        Label descLabel = new Label(type.getDescription());
        descLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-style: italic;");

        HBox footer = new HBox(10);
        footer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label priceLabel = new Label("Prix : " + type.getPrice() + "€");
        priceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");

        Button buyBtn = new Button();

        // --- LOGIQUE DE VÉRIFICATION (ACHAT / NIVEAU / ACQUIS) ---
        boolean purchased = UpgradeService.getInstance().hasUpgrade(type);
        int playerLevel = LevelService.getInstance().currentLevelProperty().get();

        if (purchased) {
            buyBtn.setText("ACQUIS");
            buyBtn.setDisable(true);
            buyBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 10;");
        }
        else if (playerLevel < type.getMinLevel()) {
            buyBtn.setText("NIV. " + type.getMinLevel() + " REQUIS");
            buyBtn.setDisable(true);
            buyBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 10;");
        }
        else {
            buyBtn.setText("ACHETER");
            buyBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 10; -fx-font-weight: bold;");
            buyBtn.setOnAction(e -> {
                if (UpgradeService.getInstance().buyUpgrade(type)) {
                    updateUI();
                    if (mainController != null) mainController.refreshInventoryUI();
                }
            });
        }

        // Utilisation d'un Pane invisible pour pousser le bouton à droite
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        footer.getChildren().addAll(priceLabel, spacer, buyBtn);

        card.getChildren().addAll(nameLabel, descLabel, footer);
        return card;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) upgradeContainer.getScene().getWindow();
        stage.close();
    }
}