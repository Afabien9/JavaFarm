package main.java.service;

import main.java.model.Plot;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class BuildService {
    private static BuildService instance;
    private boolean buildModeActive = false;

    private BuildService() {}

    public static BuildService getInstance() {
        if (instance == null) instance = new BuildService();
        return instance;
    }

    public void setBuildMode(boolean active) { this.buildModeActive = active; }

    public void buyAndPlacePlot(Pane gameWorld, double x, double y) {
        if (GameService.getInstance().getWallet().spendMoney(100)) { // Prix d'une parcelle libre
            Plot newPlot = new Plot(false);
            GameService.getInstance().getAllPlots().add(newPlot);


            Rectangle rect = new Rectangle(80, 80, Color.BROWN);
            rect.setX(x);
            rect.setY(y);
            gameWorld.getChildren().add(rect);
        }
    }
}