package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.model.*;
import main.java.service.GameService;
import main.java.service.SelectionService;

import java.io.IOException;

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

        rect.setOnMouseClicked(event -> {
            if (!mainController.isBuildModeActive()) {
                handlePlotInteraction(plotModel, rect);
                event.consume();
            }
        });

        plotModel.growthStageProperty().addListener((obs, oldStage, newStage) -> {
            updatePlotVisual(plotModel, rect, newStage.intValue());
        });

        mapPane.getChildren().add(rect);
    }

    private void handlePlotInteraction(Plot plot, Rectangle rect) {
        if (plot.getState() == PlotState.EMPTY) {
            CropType selected = SelectionService.getInstance().getSelectedCrop();
            if (selected != null) {
                GameService.getInstance().plantCrop(plot, selected);
                // FORCE le changement de couleur instantané en bleu (stade 0)
                updatePlotVisual(plot, rect, 0);
                mainController.refreshInventoryUI();
            }
        } else if (plot.getState() == PlotState.READY) {
            GameService.getInstance().harvestCrop(plot);
            mainController.refreshInventoryUI();
        }
    }

    private void updatePlotVisual(Plot plot, Rectangle rect, int stage) {
        if (plot.getState() == PlotState.EMPTY) {
            rect.setFill(Color.BROWN);
            return;
        }

        switch (stage) {
            case 0:
                rect.setFill(Color.CORNFLOWERBLUE); // BLEU INSTANTANÉ
                break;
            case 1:
                rect.setFill(Color.LIMEGREEN);
                break;
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
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.setStroke(Color.web("#2c3e50"));
        rect.setStrokeWidth(2);

        rect.setOnMouseClicked(event -> {
            if (!mainController.isBuildModeActive()) {
                handleEnclosureInteraction(enclosureModel, rect);
                event.consume();
            }
        });

        enclosureModel.stateProperty().addListener((obs, old, newState) -> {
            if (newState == PlotState.READY) {
                rect.setStroke(Color.YELLOW);
                rect.setStrokeWidth(4);
            } else {
                rect.setStroke(Color.web("#2c3e50"));
                rect.setStrokeWidth(2);
            }
        });

        mapPane.getChildren().add(rect);
    }

    private void handleEnclosureInteraction(Enclosure enc, Rectangle rect) {
        if (enc.getState() == PlotState.EMPTY) {
            AnimalType selectedAnimal = SelectionService.getInstance().getSelectedAnimal();
            if (selectedAnimal != null) {
                GameService.getInstance().buyAnimal(enc, selectedAnimal);
                if (enc.getCurrentAnimal() != null) {
                    rect.setFill(selectedAnimal.getAnimalColor());
                    mainController.refreshInventoryUI();
                }
            }
        } else if (enc.getState() == PlotState.READY) {
            GameService.getInstance().collectFromEnclosure(enc);
            mainController.refreshInventoryUI();
        } else {
            openFeedingWindow(enc);
        }
    }

    private void openFeedingWindow(Enclosure enc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AnimalInteractionView.fxml"));
            Parent root = loader.load();
            AnimalInteractionController ctrl = loader.getController();
            ctrl.setEnclosure(enc);

            Stage stage = new Stage();
            stage.setTitle("Nourrir l'animal");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            mainController.refreshInventoryUI();
        } catch (IOException e) { e.printStackTrace(); }
    }
}