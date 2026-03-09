package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import main.java.model.*;
import main.java.service.GameService;
import main.java.service.SelectionService;

/**
 * Gère l'affichage de la ferme et les interactions avec les parcelles et les enclos.
 */
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
     * Gère le clic sur le fond de la carte pour construire des structures.
     */
    @FXML
    private void handleLocalMapClick(MouseEvent event) {
        if (mainController != null && mainController.isBuildModeActive()) {
            // On vérifie quel type de structure est actif dans le mode construction
            if (mainController.getActiveStructureType() == MainController.StructureType.PLOT) {
                placeNewPlot(event.getX(), event.getY());
            } else if (mainController.getActiveStructureType() == MainController.StructureType.ENCLOSURE) {
                placeNewEnclosure(event.getX(), event.getY());
            }
        }
    }

    // --- LOGIQUE DES CULTURES (PLOTS) ---

    public void placeNewPlot(double x, double y) {
        if (mapPane == null) return;

        double gridX = Math.floor(x / 64) * 64;
        double gridY = Math.floor(y / 64) * 64;

        Plot plotModel = new Plot(false);
        Rectangle rect = new Rectangle(gridX, gridY, 62, 62);
        rect.setFill(Color.web("#8b4513")); // Marron terre
        rect.setStroke(Color.web("#5d2e0d"));
        rect.setStrokeWidth(1);

        rect.setOnMouseClicked(event -> {
            if (mainController != null && !mainController.isBuildModeActive()) {
                handlePlotInteraction(plotModel, rect);
                event.consume();
            }
        });

        plotModel.stateProperty().addListener((obs, oldState, newState) -> {
            updatePlotVisual(plotModel, rect, newState);
        });

        mapPane.getChildren().add(rect);
    }

    private void handlePlotInteraction(Plot plot, Rectangle rect) {
        if (this.inventory == null) return;

        CropType selected = SelectionService.getInstance().getSelectedCrop();

        if (plot.getState() == PlotState.EMPTY) {
            if (selected != null && inventory.useSeed(selected)) {
                GameService.getInstance().plantCrop(plot, selected);
                mainController.refreshInventoryUI();
            }
        } else if (plot.getState() == PlotState.READY) {
            GameService.getInstance().harvestPlot(plot);
            mainController.refreshInventoryUI();
        }
    }

    private void updatePlotVisual(Plot plot, Rectangle rect, PlotState state) {
        switch (state) {
            case EMPTY: rect.setFill(Color.web("#8b4513")); break;
            case GROWING: rect.setFill(Color.web("#27ae60")); break;
            case READY:
                if (plot.getCurrentCrop() != null) rect.setFill(plot.getCurrentCrop().getReadyColor());
                else rect.setFill(Color.GOLD);
                break;
        }
    }

    // --- LOGIQUE DES ANIMAUX (ENCLOSURES) ---

    /**
     * Place un nouvel enclos sur la grille.
     */
    public void placeNewEnclosure(double x, double y) {
        if (mapPane == null) return;

        double gridX = Math.floor(x / 64) * 64;
        double gridY = Math.floor(y / 64) * 64;

        Enclosure enclosureModel = new Enclosure();
        Rectangle rect = new Rectangle(gridX, gridY, 62, 62);
        rect.setFill(Color.web("#7f8c8d")); // Gris (Vide)
        rect.setStroke(Color.web("#2c3e50"));
        rect.setStrokeWidth(3);

        rect.setOnMouseClicked(event -> {
            if (mainController != null && !mainController.isBuildModeActive()) {
                handleEnclosureInteraction(enclosureModel, rect);
                event.consume();
            }
        });

        enclosureModel.stateProperty().addListener((obs, old, newState) -> {
            if (newState == PlotState.READY) {
                rect.setStroke(Color.YELLOW); // Brille quand le produit est prêt
            } else {
                rect.setStroke(Color.web("#2c3e50"));
            }
        });

        mapPane.getChildren().add(rect);
    }

    /**
     * Gère l'achat d'animal ou la collecte de produit.
     */
    private void handleEnclosureInteraction(Enclosure enc, Rectangle rect) {
        if (enc.getState() == PlotState.EMPTY) {
            // On récupère l'animal sélectionné (On utilise le même service de sélection pour simplifier)
            AnimalType selectedAnimal = SelectionService.getInstance().getSelectedAnimal();
            if (selectedAnimal != null) {
                GameService.getInstance().buyAnimal(enc, selectedAnimal);
                if (enc.getCurrentAnimal() != null) {
                    rect.setFill(selectedAnimal.getAnimalColor());
                    mainController.refreshInventoryUI();
                }
            } else {
                System.out.println("Veuillez sélectionner un animal dans la barre latérale.");
            }
        } else if (enc.getState() == PlotState.READY) {
            GameService.getInstance().collectFromEnclosure(enc);
            mainController.refreshInventoryUI();
        }
    }
}