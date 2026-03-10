package main.java.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;
import main.java.service.GameService;

public class Plot {
    private final ObjectProperty<PlotState> state = new SimpleObjectProperty<>(PlotState.EMPTY);
    private final BooleanProperty locked = new SimpleBooleanProperty(false);

    // 0 = Semis (Bleu), 1 = Croissance (Vert), 2 = Prêt (Récolte)
    private final IntegerProperty growthStage = new SimpleIntegerProperty(0);

    private Crop currentCrop;
    private Timeline growthTimeline;

    public Plot(boolean isLocked) {
        this.locked.set(isLocked);

        GameService.getInstance().debugModeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && state.get() == PlotState.GROWING) {
                startGrowthCycle(1.0);
            }
        });
    }

    public void plant(Crop crop) {
        if (locked.get() || state.get() != PlotState.EMPTY || crop == null) return;

        this.currentCrop = crop;
        this.state.set(PlotState.GROWING);
        this.growthStage.set(0); // Initialise à 0 (déclenche le bleu)

        double totalTime = GameService.getInstance().isDebugActive() ? 1.0 : crop.getGrowthTime();
        startGrowthCycle(totalTime);
    }

    private void startGrowthCycle(double totalTime) {
        if (growthTimeline != null) growthTimeline.stop();

        growthTimeline = new Timeline(
                new KeyFrame(Duration.seconds(totalTime / 2), event -> {
                    this.growthStage.set(1); // Passage au vert
                }),
                new KeyFrame(Duration.seconds(totalTime), event -> {
                    this.growthStage.set(2); // Prêt pour récolte
                    this.state.set(PlotState.READY);
                })
        );
        growthTimeline.setCycleCount(1);
        growthTimeline.play();
    }

    public void harvest() {
        if (state.get() == PlotState.READY) {
            this.currentCrop = null;
            this.state.set(PlotState.EMPTY);
            this.growthStage.set(0);
            if (growthTimeline != null) growthTimeline.stop();
        }
    }

    public PlotState getState() { return state.get(); }
    public ObjectProperty<PlotState> stateProperty() { return state; }
    public IntegerProperty growthStageProperty() { return growthStage; }
    public Crop getCurrentCrop() { return currentCrop; }
    public void setLocked(boolean b) { this.locked.set(b); }
}