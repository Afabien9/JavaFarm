package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import main.java.model.*;
import main.java.service.GameService;
import main.java.service.LevelService;

public class MapController {

    @FXML private Pane mapPane;
    private MainController mainController;
    private Inventory inventory;

    private final int CELL_SIZE = 80;
    private final int GAP = 2;
    private final int GRID_STEP = CELL_SIZE + GAP;

    private int currentPlotCount = 0;
    private int currentEnclosureCount = 0;

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
        int max = LevelService.getInstance().getMaxPlots();
        if (currentPlotCount >= max) {
            System.out.println("[LIMIT] Max parcelles atteint (" + max + ").");
            return;
        }

        int price = GameService.getInstance().getPlotPrice();
        if (GameService.getInstance().getWallet().getMoney() < price) {
            System.out.println("[ECONOMY] Pas assez d'argent (" + price + "€ requis).");
            return;
        }

        GameService.getInstance().getWallet().spendMoney(price);
        currentPlotCount++;
        mainController.updateStatusLabel();

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
            if (mainController.isDestructionModeActive()) {
                mapPane.getChildren().remove(rect);
                currentPlotCount--;
                GameService.getInstance().refundStructure(true);
                mainController.updateStatusLabel();
            } else if (!mainController.isBuildModeActive()) {
                handlePlotInteraction(plotModel);
                event.consume();
            }
        });

        plotModel.stateProperty().addListener((obs, oldState, newState) -> {
            updatePlotVisual(plotModel, rect, plotModel.growthStageProperty().get());
        });

        plotModel.growthStageProperty().addListener((obs, oldStage, newStage) -> {
            updatePlotVisual(plotModel, rect, newStage.intValue());
        });

        mapPane.getChildren().add(rect);
    }

    private void handlePlotInteraction(Plot plot) {
        if (plot.getState() == PlotState.EMPTY) {
            mainController.openPlantingMenu(plot);
        } else if (plot.getState() == PlotState.READY) {
            GameService.getInstance().harvestPlot(plot);
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
                if (plot.getCurrentCrop() != null && plot.getCurrentCrop().getType() != null) {
                    rect.setFill(plot.getCurrentCrop().getType().getReadyColor());
                } else {
                    rect.setFill(Color.GOLD);
                }
                break;
        }
    }

    public void placeNewEnclosure(double x, double y) {
        int max = LevelService.getInstance().getMaxEnclosures();
        if (currentEnclosureCount >= max) {
            System.out.println("[LIMIT] Max enclos atteint (" + max + ").");
            return;
        }

        int price = GameService.getInstance().getEnclosurePrice();
        if (GameService.getInstance().getWallet().getMoney() < price) return;

        GameService.getInstance().getWallet().spendMoney(price);
        currentEnclosureCount++;
        mainController.updateStatusLabel();

        double snapX = Math.floor(x / GRID_STEP) * GRID_STEP;
        double snapY = Math.floor(y / GRID_STEP) * GRID_STEP;

        Enclosure enclosureModel = new Enclosure();
        StackPane stack = new StackPane();
        stack.setLayoutX(snapX);
        stack.setLayoutY(snapY);

        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE, Color.web("#bdc3c7"));
        rect.setArcWidth(20);
        rect.setArcHeight(20);
        rect.setStroke(Color.web("#7f8c8d"));
        rect.setStrokeWidth(2);

        Label emojiLabel = new Label("");
        emojiLabel.setStyle("-fx-font-size: 35;");

        enclosureModel.currentAnimalProperty().addListener((obs, oldAnimal, newAnimal) -> {
            if (newAnimal != null) {
                emojiLabel.setText(getAnimalEmoji(newAnimal.getName()));
            } else {
                emojiLabel.setText("");
            }
        });

        stack.getChildren().addAll(rect, emojiLabel);

        stack.setOnMouseClicked(event -> {
            if (mainController.isDestructionModeActive()) {
                mapPane.getChildren().remove(stack);
                currentEnclosureCount--;
                GameService.getInstance().refundStructure(false);
                mainController.updateStatusLabel();
            } else if (!mainController.isBuildModeActive()) {
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

        mapPane.getChildren().add(stack);
    }

    private void handleEnclosureInteraction(Enclosure enc, Rectangle rect) {
        if (enc.getState() == PlotState.EMPTY) {
            mainController.openAnimalPlacementMenu(enc);
        } else if (enc.getState() == PlotState.READY) {
            GameService.getInstance().collectFromEnclosure(enc);
            mainController.refreshInventoryUI();
        } else {
            openAnimalInterface(enc);
        }
    }

    private String getAnimalEmoji(String name) {
        if (name == null) return "";
        String n = name.toLowerCase();
        if (n.contains("poule")) return "🐔";
        if (n.contains("vache")) return "🐄";
        if (n.contains("mouton")) return "🐑";
        if (n.contains("cochon")) return "🐷";
        return "🐾";
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

    public int getCurrentPlotCount() { return currentPlotCount; }
    public int getCurrentEnclosureCount() { return currentEnclosureCount; }
}