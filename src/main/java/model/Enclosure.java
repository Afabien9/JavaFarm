package main.java.model;

import javafx.beans.property.*;

public class Enclosure {

    private final ObjectProperty<Animal> currentAnimal = new SimpleObjectProperty<>(null);
    private final ObjectProperty<PlotState> state = new SimpleObjectProperty<>(PlotState.EMPTY);
    private final DoubleProperty productionProgress = new SimpleDoubleProperty(0.0);

    public Enclosure() {
    }

    public void setAnimal(Animal animal) {
        this.currentAnimal.set(animal);
        if (animal != null) {
            this.state.set(PlotState.GROWING);
        } else {
            this.state.set(PlotState.EMPTY);
        }
    }

    public void addAnimal(AnimalType type) {
        if (type != null) {
            Animal animal = new Animal(type);
            this.currentAnimal.set(animal);
            this.state.set(PlotState.GROWING);
            this.productionProgress.set(0.0);
            System.out.println("[ENCLOSURE] " + type.getName() + " ajouté.");
        }
    }

    public void clearEnclosure() {
        this.currentAnimal.set(null);
        this.state.set(PlotState.EMPTY);
        this.productionProgress.set(0.0);
    }

    public void collectProduct() {
        if (this.state.get() == PlotState.READY) {
            Animal animal = currentAnimal.get();
            if (animal != null) {
                animal.resetProduction();
                this.state.set(PlotState.GROWING);
                this.productionProgress.set(0.0);
            }
        }
    }

    public ObjectProperty<Animal> currentAnimalProperty() {
        return currentAnimal;
    }

    public Animal getCurrentAnimal() {
        return currentAnimal.get();
    }

    public ObjectProperty<PlotState> stateProperty() {
        return state;
    }

    public PlotState getState() {
        return state.get();
    }

    public boolean isHungry() {
        return currentAnimal.get() != null && currentAnimal.get().isHungry();
    }

    public DoubleProperty productionProgressProperty() {
        return productionProgress;
    }

    public double getProductionProgress() {
        return productionProgress.get();
    }

    public void setProductionProgress(double progress) {
        this.productionProgress.set(progress);
    }

    public BooleanProperty isHungryProperty() {
        if (currentAnimal.get() != null) {
            return currentAnimal.get().isHungryProperty();
        }
        return new SimpleBooleanProperty(false);
    }

    public int getVisualStage() {
        Animal animal = currentAnimal.get();
        if (animal == null) return 1;
        if (animal.isHungry()) return 1;
        if (animal.isReadyToProduce()) return 3;
        return 2;
    }

    public void startProductionAfterFeeding() {
        Animal animal = currentAnimal.get();
        if (animal != null && animal.isHungry()) {
            animal.feed();
            this.productionProgress.set(0.0);
            this.state.set(PlotState.GROWING);
            System.out.println("[ENCLOSURE] " + animal.getName() + " commence à produire !");
        }
    }
}