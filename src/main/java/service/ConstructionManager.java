package main.java.service;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;


public class ConstructionManager {
    private static ConstructionManager instance;

    private ConstructionManager() {}

    public static ConstructionManager getInstance() {
        if (instance == null) {
            instance = new ConstructionManager();
        }
        return instance;
    }


    public void executeConstruction(KeyCode code, Pane gameWorld, double x, double y) {
        if (code == null) return;

        switch (code) {
            case P:

                PlacementService.getInstance().buyAndPlacePlot(gameWorld, x, y);
                break;

            case E:

                PlacementService.getInstance().buyAndPlaceEnclosure(gameWorld, x, y);
                break;

            case T:

                LandService.getInstance().buyExpansion(gameWorld, x, y);
                break;

            default:

                break;
        }
    }
}