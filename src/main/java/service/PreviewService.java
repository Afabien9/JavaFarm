package main.java.service;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PreviewService {
    private static PreviewService instance;
    private Rectangle ghost;


    private final int GRID_SIZE = 80;
    private final int GAP = 10;
    private final int CELL_TOTAL = GRID_SIZE + GAP; // 90px

    private PreviewService() {}

    public static PreviewService getInstance() {
        if (instance == null) instance = new PreviewService();
        return instance;
    }


    public void updatePreview(Pane gameWorld, double x, double y, boolean active) {

        if (!active) {
            if (ghost != null) {
                gameWorld.getChildren().remove(ghost);
                ghost = null;
            }
            return;
        }


        if (ghost == null) {
            ghost = new Rectangle(GRID_SIZE, GRID_SIZE);
            ghost.setFill(Color.web("#f1c40f", 0.4));
            ghost.setStroke(Color.WHITE);
            ghost.setStrokeWidth(2);


            ghost.setMouseTransparent(true);

            gameWorld.getChildren().add(ghost);
        }


        double snapX = Math.floor(x / CELL_TOTAL) * CELL_TOTAL;
        double snapY = Math.floor(y / CELL_TOTAL) * CELL_TOTAL;

        ghost.setX(snapX);
        ghost.setY(snapY);
    }
}