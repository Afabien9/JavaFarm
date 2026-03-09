package main.java.service;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class CollisionService {
    private static CollisionService instance;

    public static CollisionService getInstance() {
        if (instance == null) instance = new CollisionService();
        return instance;
    }

    public boolean isAreaOccupied(Pane gameWorld, double x, double y, double w, double h) {
        for (Node node : gameWorld.getChildren()) {
            // 1. ON IGNORE LE FANTÔME : Si l'objet est transparent aux clics, ce n'est pas un obstacle
            if (node.isMouseTransparent()) {
                continue;
            }

            // 2. VÉRIFICATION DE LA ZONE
            // On vérifie si les limites de l'objet existant touchent la zone demandée
            if (node.getBoundsInParent().intersects(x, y, w, h)) {
                return true; // C'est une vraie parcelle ou un vrai enclos
            }
        }
        return false;
    }
}