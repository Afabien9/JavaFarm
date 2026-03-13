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

            if (node.isMouseTransparent()) {
                continue;
            }


            if (node.getBoundsInParent().intersects(x, y, w, h)) {
                return true;
            }
        }
        return false;
    }
}