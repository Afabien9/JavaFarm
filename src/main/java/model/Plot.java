package main.java.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;
import main.java.service.GameService;
import main.java.service.UpgradeService; // Importation du nouveau service d'améliorations [cite: 20]

public class Plot {
    private final ObjectProperty<PlotState> state = new SimpleObjectProperty<>(PlotState.EMPTY);
    private final BooleanProperty locked = new SimpleBooleanProperty(false);

    // 0 = Semis (Bleu), 1 = Croissance (Vert), 2 = Prêt (Récolte) [cite: 18, 19]
    private final IntegerProperty growthStage = new SimpleObjectProperty<>(0).getValue() instanceof Integer ? new SimpleIntegerProperty(0) : new SimpleIntegerProperty(0);
    private final IntegerProperty growthStageFinal = new SimpleIntegerProperty(0);

    private Crop currentCrop;
    private Timeline growthTimeline;

    public Plot(boolean isLocked) {
        this.locked.set(isLocked);

        // Écouteur pour le mode Debug [cite: 1, 9]
        GameService.getInstance().debugModeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && state.get() == PlotState.GROWING) {
                startGrowthCycle(1.0);
            }
        });
    }

    /**
     * Plante une culture et calcule le temps de pousse en fonction des améliorations.
     */
    public void plant(CropType type) {
        if (locked.get() || state.get() != PlotState.EMPTY || type == null) return;

        // Création de l'objet Crop basé sur le type sélectionné
        this.currentCrop = new Crop(
                type.getName(),
                type.getGrowthTime(),
                type.getBuyPrice(),
                (int)(type.getBuyPrice() * 1.5)
        );
        this.currentCrop.setType(type);

        this.state.set(PlotState.GROWING);
        this.growthStageFinal.set(0); // Stade initial (Bleu) [cite: 18, 19]

        // Calcul du temps : Debug ou (Temps Normal * Multiplicateur d'amélioration) [cite: 9, 20]
        double modifier = UpgradeService.getInstance().getModifier(UpgradeType.CROP_SPEED);
        double totalTime = GameService.getInstance().isDebugActive() ? 1.0 : (currentCrop.getGrowthTime() * modifier);

        startGrowthCycle(totalTime);
    }

    /**
     * Gère le cycle de vie visuel de la plante divisé en deux étapes.
     */
    private void startGrowthCycle(double totalTime) {
        if (growthTimeline != null) growthTimeline.stop();

        growthTimeline = new Timeline(
                new KeyFrame(Duration.seconds(totalTime / 2), event -> {
                    this.growthStageFinal.set(1); // Étape intermédiaire (Vert) [cite: 18, 19]
                }),
                new KeyFrame(Duration.seconds(totalTime), event -> {
                    this.growthStageFinal.set(2); // Étape finale (Prêt) [cite: 18, 19]
                    this.state.set(PlotState.READY);
                })
        );
        growthTimeline.setCycleCount(1);
        growthTimeline.play();
    }

    /**
     * Réinitialise la parcelle après la récolte.
     */
    public void harvest() {
        if (state.get() == PlotState.READY) {
            this.currentCrop = null;
            this.state.set(PlotState.EMPTY);
            this.growthStageFinal.set(0);
            if (growthTimeline != null) growthTimeline.stop();
        }
    }

    // --- GETTERS ET PROPERTIES ---

    public PlotState getState() { return state.get(); }
    public ObjectProperty<PlotState> stateProperty() { return state; }

    /**
     * Propriété observée par le MapController pour changer la couleur du rectangle. [cite: 18]
     */
    public IntegerProperty growthStageProperty() { return growthStageFinal; }

    public Crop getCurrentCrop() { return currentCrop; }

    public void setLocked(boolean b) { this.locked.set(b); }
    public boolean isLocked() { return locked.get(); }
}