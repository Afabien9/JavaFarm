package main.java.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import main.java.model.*;
import main.java.service.GameService;
import main.java.service.LevelService;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class MapController {

    @FXML private Pane mapPane;
    private MainController mainController;
    private Inventory inventory;

    private final int CELL_SIZE = 70;
    private final int GAP = 5;
    private final int GRID_STEP = CELL_SIZE + GAP;

    private int currentPlotCount = 0;
    private int currentEnclosureCount = 0;
    private final Set<String> occupiedPositions = new HashSet<>();
    private final Map<String, Enclosure> enclosureMap = new HashMap<>();

    private Image terreImage;
    private Image enclosImage;

    @FXML
    public void initialize() {
        this.terreImage = loadImage("terre");
        this.enclosImage = loadImage("enclos");
    }

    private Image loadImage(String name) {
        try {
            String[] extensions = {".png", ".jpg"};
            for (String ext : extensions) {
                var stream = getClass().getResourceAsStream("/images/" + name + ext);
                if (stream != null) return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("[ERREUR] Image introuvable : " + name);
        }
        return null;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setupData(Wallet wallet, Inventory inventory) {
        this.inventory = inventory;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }

    @FXML
    private void handleLocalMapClick(MouseEvent event) {
        if (mainController != null && mainController.isBuildModeActive()) {
            double snapX = Math.floor(event.getX() / GRID_STEP) * GRID_STEP;
            double snapY = Math.floor(event.getY() / GRID_STEP) * GRID_STEP;

            if (mainController.getActiveStructureType() == MainController.StructureType.PLOT) {
                placeNewPlot(snapX, snapY);
            } else if (mainController.getActiveStructureType() == MainController.StructureType.ENCLOSURE) {
                placeNewEnclosure(snapX, snapY);
            }
        }
    }

    public void placeNewPlot(double x, double y) {
        if (isAreaOccupied(x, y)) return;

        int maxPlots = LevelService.getInstance().getMaxPlots();
        if (currentPlotCount >= maxPlots) {
            showAlert("Limite atteinte", "Niveau actuel : max " + maxPlots + " parcelles.");
            return;
        }

        int price = GameService.getInstance().getNextPlotPrice(currentPlotCount);
        if (GameService.getInstance().getWallet().getMoney() < price) {
            showAlert("Pas assez d'argent", "Il vous faut " + price + " €.");
            return;
        }

        if (!showConfirmation("Achat", "Nouvelle parcelle", "Coût : " + price + " €")) return;

        GameService.getInstance().getWallet().spendMoney(price);
        loadPlot(x, y);
    }

    public void loadPlot(double x, double y) {
        String posKey = (int)x + "_" + (int)y + "_plot";
        occupiedPositions.add(posKey);
        currentPlotCount++;
        if (mainController != null) mainController.updateStatusLabel();

        Plot plotModel = new Plot(false);
        StackPane container = createBaseContainer(x, y);

        ImageView plotView = new ImageView();
        plotView.setFitWidth(CELL_SIZE);
        plotView.setFitHeight(CELL_SIZE);
        plotView.setPreserveRatio(true);

        container.getChildren().add(plotView);
        updatePlotVisual(plotModel, plotView, 0);

        container.setOnMouseClicked(event -> {
            if (mainController.isDestructionModeActive()) {
                mapPane.getChildren().remove(container);
                occupiedPositions.remove(posKey);
                currentPlotCount--;
                mainController.updateStatusLabel();
                event.consume();
            } else if (!mainController.isBuildModeActive()) {
                handlePlotInteraction(plotModel, plotView);
                event.consume();
            }
        });

        plotModel.growthStageProperty().addListener((obs, old, newS) -> updatePlotVisual(plotModel, plotView, newS.intValue()));
        plotModel.stateProperty().addListener((obs, old, newS) -> updatePlotVisual(plotModel, plotView, plotModel.getGrowthStage()));

        mapPane.getChildren().add(container);
    }

    public void placeNewEnclosure(double x, double y) {
        if (isAreaOccupied(x, y)) return;

        int maxEnclosures = LevelService.getInstance().getMaxEnclosures();
        if (currentEnclosureCount >= maxEnclosures) {
            showAlert("Limite atteinte", "Niveau actuel : max " + maxEnclosures + " enclos.");
            return;
        }

        int price = GameService.getInstance().getNextEnclosurePrice(currentEnclosureCount);
        if (GameService.getInstance().getWallet().getMoney() < price) {
            showAlert("Pas assez d'argent", "Il vous faut " + price + " €.");
            return;
        }

        if (!showConfirmation("Construction", "Nouvel Enclos", "Coût : " + price + " €")) return;

        GameService.getInstance().getWallet().spendMoney(price);
        loadEnclosure(x, y, null);
    }

    public void loadEnclosure(double x, double y, String animalType) {
        String posKey = (int)x + "_" + (int)y + "_enclosure";
        occupiedPositions.add(posKey);
        currentEnclosureCount++;

        Enclosure enclosureModel = new Enclosure();
        enclosureMap.put(posKey, enclosureModel);

        if (animalType != null && !animalType.isEmpty()) {
            try {
                AnimalType type = AnimalType.valueOf(animalType.toUpperCase());
                enclosureModel.setAnimal(new Animal(type));
            } catch (Exception e) {
                System.err.println("Erreur chargement animal: " + animalType);
            }
        }

        GameService.getInstance().registerEnclosure(enclosureModel);
        if (mainController != null) mainController.updateStatusLabel();

        StackPane container = createBaseContainer(x, y);
        ImageView bgView = new ImageView(enclosImage != null ? enclosImage : terreImage);
        bgView.setFitWidth(CELL_SIZE);
        bgView.setFitHeight(CELL_SIZE);

        ImageView animalView = new ImageView();
        animalView.setFitWidth(CELL_SIZE * 0.8);
        animalView.setFitHeight(CELL_SIZE * 0.8);
        animalView.setPreserveRatio(true);
        animalView.setMouseTransparent(true);

        Label stateLabel = new Label("");
        stateLabel.setStyle("-fx-font-size: 16; -fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 5;");
        StackPane.setAlignment(stateLabel, Pos.TOP_RIGHT);

        enclosureModel.currentAnimalProperty().addListener((obs, old, newA) -> {
            Platform.runLater(() -> updateEnclosureVisual(enclosureModel, animalView, stateLabel));
            if (newA != null) {
                newA.isHungryProperty().addListener((o, ol, h) -> Platform.runLater(() -> updateEnclosureVisual(enclosureModel, animalView, stateLabel)));
                newA.isReadyToProduceProperty().addListener((o, ol, r) -> Platform.runLater(() -> updateEnclosureVisual(enclosureModel, animalView, stateLabel)));
            }
        });

        if (enclosureModel.getCurrentAnimal() != null) {
            updateEnclosureVisual(enclosureModel, animalView, stateLabel);
        }

        container.getChildren().addAll(bgView, animalView, stateLabel);

        container.setOnMouseClicked(event -> {
            if (mainController.isDestructionModeActive()) {
                mapPane.getChildren().remove(container);
                occupiedPositions.remove(posKey);
                enclosureMap.remove(posKey);
                currentEnclosureCount--;
                mainController.updateStatusLabel();
                event.consume();
            } else if (!mainController.isBuildModeActive()) {
                if (enclosureModel.getCurrentAnimal() == null) {
                    mainController.openAnimalPlacementMenu(enclosureModel);
                } else {
                    mainController.handleAnimalInteraction(enclosureModel);
                }
                event.consume();
            }
        });

        mapPane.getChildren().add(container);
    }

    public String getAnimalAt(String posKey) {
        Enclosure enc = enclosureMap.get(posKey);
        if (enc != null && enc.getCurrentAnimal() != null) {
            return enc.getCurrentAnimal().getType().name();
        }
        return null;
    }

    private boolean isAreaOccupied(double x, double y) {
        String base = (int)x + "_" + (int)y;
        return occupiedPositions.contains(base + "_plot") || occupiedPositions.contains(base + "_enclosure");
    }

    private StackPane createBaseContainer(double x, double y) {
        StackPane container = new StackPane();
        container.setLayoutX(x);
        container.setLayoutY(y);
        container.setPrefSize(CELL_SIZE, CELL_SIZE);
        container.setAlignment(Pos.CENTER);
        Rectangle mask = new Rectangle(CELL_SIZE, CELL_SIZE);
        mask.setArcWidth(20); mask.setArcHeight(20);
        container.setClip(mask);
        return container;
    }

    private void updateEnclosureVisual(Enclosure enclosure, ImageView animalView, Label stateLabel) {
        Animal animal = enclosure.getCurrentAnimal();
        if (animal == null) {
            animalView.setImage(null);
            stateLabel.setText("");
            return;
        }
        int stage = animal.isReadyToProduce() ? 3 : (!animal.isHungry() ? 2 : 1);
        stateLabel.setText(animal.isReadyToProduce() ? "📦" : (!animal.isHungry() ? "⏳" : "🍴"));
        animalView.setImage(loadImage(animal.getType().getName().toLowerCase() + stage));
    }

    private void updatePlotVisual(Plot plot, ImageView view, int stage) {
        if (plot.getState() == PlotState.EMPTY) {
            view.setImage(terreImage);
        } else {
            String baseName = plot.getCurrentCrop().getType().getName().toLowerCase().replace("é", "e").replace("ï", "i").replace("tte", "te");
            if (baseName.equals("ble")) baseName = "Ble";
            view.setImage(loadImage(baseName + Math.min(stage + 1, 3)));
        }
    }

    private void handlePlotInteraction(Plot plot, ImageView view) {
        if (plot.getState() == PlotState.EMPTY) {
            mainController.openPlantingMenu(plot);
        } else if (plot.getState() == PlotState.READY) {
            GameService.getInstance().harvestPlot(plot);
            plot.setState(PlotState.EMPTY);
            plot.setGrowthStage(0);
            updatePlotVisual(plot, view, 0);
            mainController.refreshInventoryUI();
        }
    }

    public Set<String> getOccupiedPositions() { return occupiedPositions; }
    public int getCurrentPlotCount() { return currentPlotCount; }
    public int getCurrentEnclosureCount() { return currentEnclosureCount; }
}