package main.java.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.java.model.*;

public class InventoryController {

    @FXML private FlowPane itemsGrid; // Utilise un FlowPane pour une grille fluide
    @FXML private Label titleLabel;

    private Inventory inventory;
    private MainController mainController;

    public void setData(Inventory inventory, MainController mainController) {
        this.inventory = inventory;
        this.mainController = mainController;
        refreshUI();
    }

    private void refreshUI() {
        itemsGrid.getChildren().clear();

        // 1. Affichage des Graines
        inventory.getSeeds().forEach((type, qty) -> {
            if (qty > 0) itemsGrid.getChildren().add(createItemCard(type.getName(), qty, "🌱", "#2ecc71"));
        });

        // 2. Affichage des Animaux en stock
        inventory.getAnimals().forEach((type, qty) -> {
            if (qty > 0) itemsGrid.getChildren().add(createItemCard(type.getName(), qty, getAnimalEmoji(type.getName()), "#3498db"));
        });

        // 3. Affichage des Récoltes
        inventory.getProducts().forEach((type, qty) -> {
            if (qty > 0) itemsGrid.getChildren().add(createItemCard(type.getName(), qty, "📦", "#f1c40f"));
        });

        // 4. Produits animaux
        inventory.getAnimalProducts().forEach((name, qty) -> {
            if (qty > 0) itemsGrid.getChildren().add(createItemCard(name, qty, "✨", "#9b59b6"));
        });
    }

    private VBox createItemCard(String name, int qty, String emoji, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(100, 100);
        card.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 10;");

        Label iconLabel = new Label(emoji);
        iconLabel.setStyle("-fx-font-size: 30;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");

        Label qtyLabel = new Label("x" + qty);
        qtyLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

        card.getChildren().addAll(iconLabel, nameLabel, qtyLabel);
        return card;
    }

    private String getAnimalEmoji(String name) {
        String n = name.toLowerCase();
        if (n.contains("poule")) return "🐔";
        if (n.contains("vache")) return "🐄";
        if (n.contains("mouton")) return "🐑";
        return "🐾";
    }

    @FXML
    private void handleClose() {
        ((Stage) itemsGrid.getScene().getWindow()).close();
    }
}