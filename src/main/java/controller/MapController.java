package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import main.java.model.*;
import main.java.service.GameService;
import main.java.service.SelectionService;

public class MapController {

    @FXML private Pane mapPane;

    private MainController mainController;
    private Inventory inventory;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setupData(Wallet wallet, Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * CETTE MÉTHODE MANQUAIT : C'est l'entrée du clic sur le fond de la carte.
     */
    @FXML
    private void handleLocalMapClick(MouseEvent event) {
        if (mainController != null && mainController.isBuildModeActive()) {
            placeNewPlot(event.getX(), event.getY());
        }
    }

    /**
     * Crée physiquement une parcelle sur la grille.
     */
    public void placeNewPlot(double x, double y) {
        if (mapPane == null) return;

        double gridX = Math.floor(x / 64) * 64;
        double gridY = Math.floor(y / 64) * 64;

        Plot plotModel = new Plot(false);
        Rectangle rect = new Rectangle(gridX, gridY, 64, 64);
        rect.setFill(Color.web("#8b4513"));
        rect.setStroke(Color.web("#5d2e0d"));

        // Interaction avec la parcelle déjà posée
        rect.setOnMouseClicked(event -> {
            if (mainController != null && !mainController.isBuildModeActive()) {
                handlePlotInteraction(plotModel, rect);
                event.consume();
            }
        });

        // Mise à jour visuelle automatique lors de la croissance
        plotModel.stateProperty().addListener((obs, oldState, newState) -> {
            updatePlotVisual(plotModel, rect, newState);
        });

        mapPane.getChildren().add(rect);
    }

    /**
     * Logique de plantation et récolte.
     */
    private void handlePlotInteraction(Plot plot, Rectangle rect) {
        CropType selected = SelectionService.getInstance().getSelectedCrop();

        if (plot.getState() == PlotState.EMPTY) {
            if (selected != null && inventory != null && inventory.useSeed(selected)) {
                GameService.getInstance().plantCrop(plot, selected);
                mainController.refreshInventoryUI();
                System.out.println("SUCCÈS : Planté " + selected.getName());
            } else if (selected == null) {
                System.out.println("INFO : Sélectionnez une graine dans l'inventaire avant de cliquer sur la terre.");
            } else {
                System.out.println("ERREUR : Pas assez de graines de " + (selected != null ? selected.getName() : "inconnu"));
            }
        }
        else if (plot.getState() == PlotState.READY) {
            GameService.getInstance().harvestPlot(plot);
            mainController.refreshInventoryUI();
            System.out.println("RÉCOLTE : Terminée !");
        }
    }

    private void updatePlotVisual(Plot plot, Rectangle rect, PlotState state) {
        switch (state) {
            case EMPTY:
                rect.setFill(Color.web("#8b4513"));
                break;
            case GROWING:
                rect.setFill(Color.web("#27ae60"));
                break;
            case READY:
                if (plot.getCurrentCrop() != null) {
                    rect.setFill(plot.getCurrentCrop().getReadyColor());
                } else {
                    rect.setFill(Color.GOLD);
                }
                break;
        }
    }
}