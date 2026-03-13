package main.java.service;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.java.model.*;

public class PlacementService {
    private static PlacementService instance;


    private final int GRID_SIZE = 80;
    private final int GAP = 10;
    private final int CELL_TOTAL = GRID_SIZE + GAP;

    public static PlacementService getInstance() {
        if (instance == null) instance = new PlacementService();
        return instance;
    }

    public void buyAndPlacePlot(Pane gameWorld, double x, double y) {
        double snapX = Math.floor(x / GRID_SIZE) * GRID_SIZE;
        double snapY = Math.floor(y / GRID_SIZE) * GRID_SIZE;

        if (CollisionService.getInstance().isAreaOccupied(gameWorld, snapX, snapY, GRID_SIZE, GRID_SIZE)) return;

        if (GameService.getInstance().getWallet().spendMoney(100)) {
            Plot plot = new Plot(false);
            Rectangle rect = new Rectangle(GRID_SIZE, GRID_SIZE, Color.BROWN);
            rect.setX(snapX); rect.setY(snapY);
            rect.setStroke(Color.BLACK);

            rect.setOnMouseClicked(e -> {
                if (plot.getState() == PlotState.EMPTY) {
                    GameService.getInstance().plantCrop(plot, SelectionService.getInstance().getSelectedCrop());
                } else if (plot.getState() == PlotState.READY) {
                    GameService.getInstance().harvestPlot(plot);
                }
            });

            plot.stateProperty().addListener((obs, oldV, newV) -> {
                if (newV == PlotState.READY) rect.setFill(Color.GOLD);
                else if (newV == PlotState.GROWING) rect.setFill(Color.GREEN);
                else rect.setFill(Color.BROWN);
            });

            gameWorld.getChildren().add(rect);
        }
    }

    public void buyAndPlaceEnclosure(Pane gameWorld, double x, double y) {

        double snapX = Math.floor(x / CELL_TOTAL) * CELL_TOTAL;
        double snapY = Math.floor(y / CELL_TOTAL) * CELL_TOTAL;


        if (CollisionService.getInstance().isAreaOccupied(gameWorld, snapX, snapY, GRID_SIZE, GRID_SIZE)) {
            System.out.println("Zone déjà occupée !");
            return;
        }


        if (GameService.getInstance().getWallet().spendMoney(500)) {
            Rectangle enclosure = new Rectangle(GRID_SIZE, GRID_SIZE);
            enclosure.setX(snapX);
            enclosure.setY(snapY);


            enclosure.setFill(Color.web("#bdc3c7"));
            enclosure.setStroke(Color.web("#7f8c8d"));
            enclosure.setStrokeWidth(3);


            enclosure.setArcWidth(15);
            enclosure.setArcHeight(15);

            gameWorld.getChildren().add(enclosure);
            System.out.println("Enclos placé avec succès.");
        } else {
            System.out.println("Pas assez d'argent pour un enclos (500€ requis).");
        }
    }
}