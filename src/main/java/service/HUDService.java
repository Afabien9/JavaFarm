package main.java.service;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

public class HUDService {

    public static VBox createHelpMenu() {
        VBox menu = new VBox(8);
        menu.setPadding(new Insets(15));
        // Fond sombre transparent pour le contraste
        menu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 12; -fx-border-color: #f1c40f; -fx-border-radius: 12; -fx-border-width: 2;");
        menu.setMouseTransparent(true); // Pour ne pas bloquer les clics sur la ferme

        Label title = new Label("AIDE À LA CONSTRUCTION");
        title.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Style commun pour les touches
        String keyStyle = "-fx-text-fill: #ecf0f1; -fx-font-family: 'Monospaced'; -fx-font-size: 12px;";

        Label pKey = new Label("[P] + Clic : Acheter Parcelle (100€)");
        Label eKey = new Label("[E] + Clic : Acheter Enclos (500€)");
        Label tKey = new Label("[T] + Clic : Agrandir Terrain (500€)");
        Label mKey = new Label("[M]        : Debug +1000€");
        Label cam = new Label("Clic Droit : Déplacer Caméra");

        pKey.setStyle(keyStyle);
        eKey.setStyle(keyStyle);
        tKey.setStyle(keyStyle);
        mKey.setStyle("-fx-text-fill: #2ecc71; -fx-font-family: 'Monospaced'; -fx-font-size: 12px;"); // Vert pour debug
        cam.setStyle(keyStyle);

        menu.getChildren().addAll(title, pKey, eKey, tKey, mKey, cam);

        return menu;
    }
}