package main.java.model;

import javafx.beans.property.*;

/**
 * Modèle représentant un enclos pour animaux.
 * Gère l'état de production, la faim de l'animal et le type d'animal présent.
 */
public class Enclosure {

    // Propriétés JavaFX pour permettre le "Binding" et les "Listeners"
    private final ObjectProperty<AnimalType> currentAnimal = new SimpleObjectProperty<>(null);
    private final ObjectProperty<PlotState> state = new SimpleObjectProperty<>(PlotState.EMPTY);
    private final BooleanProperty isHungry = new SimpleBooleanProperty(false);
    private final IntegerProperty growthStage = new SimpleIntegerProperty(0);
    private final DoubleProperty productionProgress = new SimpleDoubleProperty(0.0);

    public Enclosure() {
        // Initialisation par défaut : l'enclos est vide au départ.
    }

    // --- LOGIQUE MÉTIER ---

    /**
     * Ajoute un animal dans l'enclos.
     * Déclenche automatiquement l'état de croissance et la faim.
     */
    public void addAnimal(AnimalType type) {
        if (type != null) {
            this.currentAnimal.set(type);
            this.state.set(PlotState.GROWING);
            this.isHungry.set(true); // L'animal attend d'être nourri pour produire
            this.growthStage.set(0);
            this.productionProgress.set(0.0);
            System.out.println("[ENCLOSURE] " + type.getName() + " ajouté.");
        }
    }

    /**
     * Vide l'enclos et réinitialise les statistiques.
     */
    public void clearEnclosure() {
        this.currentAnimal.set(null);
        this.state.set(PlotState.EMPTY);
        this.isHungry.set(false);
        this.productionProgress.set(0.0);
    }

    /**
     * Appelé après la récolte d'un produit animal.
     * Remet l'animal en attente de nourriture.
     */
    public void collectProduct() {
        if (this.state.get() == PlotState.READY) {
            this.state.set(PlotState.GROWING);
            this.isHungry.set(true); // Redevient affamé après avoir produit
            this.productionProgress.set(0.0);
        }
    }

    // --- GETTERS ET PROPERTIES (Indispensables pour MapController) ---

    /**
     * Permet au MapController d'écouter les changements d'animal pour l'emoji.
     */
    public ObjectProperty<AnimalType> currentAnimalProperty() {
        return currentAnimal;
    }

    public AnimalType getCurrentAnimal() {
        return currentAnimal.get();
    }

    public ObjectProperty<PlotState> stateProperty() {
        return state;
    }

    public PlotState getState() {
        return state.get();
    }

    public BooleanProperty isHungryProperty() {
        return isHungry;
    }

    public boolean isHungry() {
        return isHungry.get();
    }

    public void setHungry(boolean hungry) {
        this.isHungry.set(hungry);
    }

    public IntegerProperty growthStageProperty() {
        return growthStage;
    }

    public int getGrowthStage() {
        return growthStage.get();
    }

    public void setGrowthStage(int stage) {
        this.growthStage.set(stage);
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

    public void startProductionAfterFeeding() {
        if (this.currentAnimal.get() != null && this.isHungry.get()) {
            // 1. L'animal n'a plus faim
            this.isHungry.set(false);

            // 2. On réinitialise la progression
            this.productionProgress.set(0.0);
            this.growthStage.set(0);

            // 3. On s'assure que l'état est bien GROWING (en production)
            this.state.set(PlotState.GROWING);

            System.out.println("[ENCLOSURE] " + currentAnimal.get().getName() + " commence à produire !");
        }
    }
}