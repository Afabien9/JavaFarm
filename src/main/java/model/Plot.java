package main.java.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;
import main.java.service.GameService;

/**
 * Gère la logique de croissance individuelle d'une parcelle.
 */
public class Plot {
    private final ObjectProperty<PlotState> state = new SimpleObjectProperty<>(PlotState.EMPTY);
    private final BooleanProperty locked = new SimpleBooleanProperty(false);
    private Crop currentCrop;
    private Timeline growthTimeline;

    public Plot(boolean isLocked) {
        this.locked.set(isLocked);

        // Écouteur pour réagir au changement de mode Debug en plein milieu d'une pousse
        GameService.getInstance().debugModeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && state.get() == PlotState.GROWING) {
                // Si on active le debug alors que ça pousse, on finit en 1s
                startTimer(1.0);
            }
        });
    }

    public void plant(Crop crop) {
        if (locked.get() || state.get() != PlotState.EMPTY || crop == null) return;

        this.currentCrop = crop;
        this.state.set(PlotState.GROWING);

        // LE "IF" QUE TU AS DEMANDÉ :
        double tempsDePousse;
        if (GameService.getInstance().isDebugActive()) {
            tempsDePousse = 1.0; // 1 seconde si debug
            System.out.println("DEBUG : Croissance rapide activée (1s)");
        } else {
            tempsDePousse = crop.getGrowthTime(); // Temps normal sinon
        }

        startTimer(tempsDePousse);
    }

    /**
     * Lance ou redémarre le minuteur avec une durée spécifique.
     */
    private void startTimer(double seconds) {
        if (growthTimeline != null) {
            growthTimeline.stop();
        }

        growthTimeline = new Timeline(new KeyFrame(
                Duration.seconds(seconds),
                event -> {
                    this.state.set(PlotState.READY);
                    System.out.println("LOG : Récolte prête !");
                }
        ));

        growthTimeline.setCycleCount(1);
        growthTimeline.play();
    }

    public void harvest() {
        if (state.get() == PlotState.READY) {
            this.currentCrop = null;
            this.state.set(PlotState.EMPTY);
            if (growthTimeline != null) growthTimeline.stop();
        }
    }

    // --- GETTERS INDISPENSABLES ---

    public PlotState getState() {
        return state.get();
    }

    public ObjectProperty<PlotState> stateProperty() {
        return state;
    }

    public Crop getCurrentCrop() {
        return currentCrop;
    }

    public void setLocked(boolean b) {
        this.locked.set(b);
    }
}