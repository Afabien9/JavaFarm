package main.java.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;
import main.java.service.GameService;

public class Enclosure {
    private final ObjectProperty<PlotState> state = new SimpleObjectProperty<>(PlotState.EMPTY);
    private final BooleanProperty isHungry = new SimpleBooleanProperty(false); // Nouvel état
    private AnimalType currentAnimal;
    private Timeline productionTimeline;

    public void addAnimal(AnimalType type) {
        if (state.get() != PlotState.EMPTY) return;
        this.currentAnimal = type;
        this.isHungry.set(true); // L'animal arrive avec faim
        this.state.set(PlotState.GROWING); // État "En attente"
    }

    /**
     * Cette méthode remplace le démarrage automatique.
     * Elle est appelée uniquement par le service de nourrissage.
     */
    public void startProductionAfterFeeding() {
        if (currentAnimal == null) return;

        isHungry.set(false);
        double time = GameService.getInstance().isDebugActive() ? 1.0 : currentAnimal.getProductionTime();

        if (productionTimeline != null) productionTimeline.stop();
        productionTimeline = new Timeline(new KeyFrame(
                Duration.seconds(time),
                event -> state.set(PlotState.READY)
        ));
        productionTimeline.play();
    }

    public void collectProduct() {
        if (state.get() == PlotState.READY) {
            state.set(PlotState.GROWING);
            isHungry.set(true); // Redevient affamé après la récolte
            // ON NE RELANCE PAS LE TIMER ICI
        }
    }

    public AnimalType getCurrentAnimal() { return currentAnimal; }
    public PlotState getState() { return state.get(); }
    public ObjectProperty<PlotState> stateProperty() { return state; }
    public BooleanProperty isHungryProperty() { return isHungry; }
}