package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import main.java.model.*;
import main.java.service.GameService;

public class MapController {

    @FXML private Pane mapPane;

    private MainController mainController;
    private Inventory inventory;

    private final int CELL_SIZE = 80;
    private final int GAP = 2;
    private final int GRID_STEP = CELL_SIZE + GAP;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setupData(Wallet wallet, Inventory inventory) {
        this.inventory = inventory;
    }

    @FXML
    private void handleLocalMapClick(MouseEvent event) {
        if (mainController != null && mainController.isBuildModeActive()) {
            if (mainController.getActiveStructureType() == MainController.StructureType.PLOT) {
                placeNewPlot(event.getX(), event.getY());
            } else if (mainController.getActiveStructureType() == MainController.StructureType.ENCLOSURE) {
                placeNewEnclosure(event.getX(), event.getY());
            }
        }
    }

    public void placeNewPlot(double x, double y) {
        double snapX = Math.floor(x / GRID_STEP) * GRID_STEP;
        double snapY = Math.floor(y / GRID_STEP) * GRID_STEP;

        Plot plotModel = new Plot(false);
        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE, Color.BROWN);
        rect.setX(snapX);
        rect.setY(snapY);
        rect.setStroke(Color.BLACK);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        rect.setOnMouseClicked(event -> {
            if (!mainController.isBuildModeActive()) {
                handlePlotInteraction(plotModel);
                event.consume();
            }
        });

        // Listener sur l'état pour forcer la mise à jour dès la plantation
        plotModel.stateProperty().addListener((obs, oldState, newState) -> {
            updatePlotVisual(plotModel, rect, plotModel.growthStageProperty().get());
        });

        // Listener sur l'étape de croissance pour changer les couleurs
        plotModel.growthStageProperty().addListener((obs, oldStage, newStage) -> {
            updatePlotVisual(plotModel, rect, newStage.intValue());
        });

        mapPane.getChildren().add(rect);
    }

    private void handlePlotInteraction(Plot plot) {
        if (plot.getState() == PlotState.EMPTY) {
            mainController.openPlantingMenu(plot);
        } else if (plot.getState() == PlotState.READY) {
            CropType harvestedType = plot.getCurrentCrop().getType();
            plot.harvest();
            inventory.addProduct(harvestedType, 1);
            mainController.refreshInventoryUI();
        }
    }

    private void updatePlotVisual(Plot plot, Rectangle rect, int stage) {
        if (plot.getState() == PlotState.EMPTY) {
            rect.setFill(Color.BROWN);
            return;
        }
        switch (stage) {
            case 0: rect.setFill(Color.CORNFLOWERBLUE); break;
            case 1: rect.setFill(Color.LIMEGREEN); break;
            case 2:
                if (plot.getCurrentCrop() != null) rect.setFill(plot.getCurrentCrop().getReadyColor());
                else rect.setFill(Color.GOLD);
                break;
        }
    }

    public void placeNewEnclosure(double x, double y) {
        double snapX = Math.floor(x / GRID_STEP) * GRID_STEP;
        double snapY = Math.floor(y / GRID_STEP) * GRID_STEP;

        Enclosure enclosureModel = new Enclosure();
        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE, Color.web("#bdc3c7"));
        rect.setX(snapX);
        rect.setY(snapY);
        rect.setArcWidth(20);
        rect.setArcHeight(20);
        rect.setStroke(Color.web("#7f8c8d"));
        rect.setStrokeWidth(2);

        rect.setOnMouseClicked(event -> {
            if (!mainController.isBuildModeActive()) {
                handleEnclosureInteraction(enclosureModel, rect);
                event.consume();
            }
        });

        enclosureModel.stateProperty().addListener((obs, old, newState) -> {
            if (newState == PlotState.READY) {
                rect.setStroke(Color.GOLD);
                rect.setStrokeWidth(4);
            } else {
                rect.setStroke(Color.web("#7f8c8d"));
                rect.setStrokeWidth(2);
            }
        });

        mapPane.getChildren().add(rect);
    }

    private void handleEnclosureInteraction(Enclosure enc, Rectangle rect) {
        if (enc.getState() == PlotState.EMPTY) {
            mainController.openShop();
        } else if (enc.getState() == PlotState.READY) {
            GameService.getInstance().collectFromEnclosure(enc);
            mainController.refreshInventoryUI();
        } else {
            openAnimalInterface(enc);
        }
    }

    private void openAnimalInterface(Enclosure enc) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/AnimalInteractionView.fxml"));
            javafx.scene.Parent root = loader.load();
            AnimalInteractionController ctrl = loader.getController();
            ctrl.setEnclosure(enc);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}