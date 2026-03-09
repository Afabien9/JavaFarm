package main.java.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;
import main.java.service.GameService;

public class Enclosure {
    private final ObjectProperty<PlotState> state = new SimpleObjectProperty<>(PlotState.EMPTY);
    private AnimalType currentAnimal;
    private Timeline productionTimeline;

    public Enclosure() {
        // Écouteur Debug : si actif, on finit la production en 1s
        GameService.getInstance().debugModeProperty().addListener((obs, old, newVal) -> {
            if (newVal && state.get() == PlotState.GROWING) {
                startTimer(1.0);
            }
        });
    }

    public void addAnimal(AnimalType type) {
        if (state.get() != PlotState.EMPTY) return;
        this.currentAnimal = type;
        this.state.set(PlotState.GROWING); // GROWING signifie ici "En production"

        double time = GameService.getInstance().isDebugActive() ? 1.0 : type.getProductionTime();
        startTimer(time);
    }

    private void startTimer(double seconds) {
        if (productionTimeline != null) productionTimeline.stop();
        productionTimeline = new Timeline(new KeyFrame(
                Duration.seconds(seconds),
                event -> state.set(PlotState.READY)
        ));
        productionTimeline.play();
    }

    public void collectProduct() {
        if (state.get() == PlotState.READY) {
            // Contrairement au Plot, l'animal reste ! On relance juste la production
            state.set(PlotState.GROWING);
            double time = GameService.getInstance().isDebugActive() ? 1.0 : currentAnimal.getProductionTime();
            startTimer(time);
        }
    }

    public PlotState getState() { return state.get(); }
    public ObjectProperty<PlotState> stateProperty() { return state; }
    public AnimalType getCurrentAnimal() { return currentAnimal; }
}