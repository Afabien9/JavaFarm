package main.java.service;

import main.java.model.AnimalType;
import main.java.model.CropType;

/**
 * Service central gérant ce que le joueur a "en main" (graine ou animal).
 * Ce service permet de faire le lien entre la Sidebar et la Map.
 */
public class SelectionService {
    private static SelectionService instance;

    private CropType selectedCrop;
    private AnimalType selectedAnimal;

    private SelectionService() {}

    public static SelectionService getInstance() {
        if (instance == null) {
            instance = new SelectionService();
        }
        return instance;
    }

    // --- GESTION DES CULTURES ---

    public void setSelectedCrop(CropType type) {
        this.selectedCrop = type;
        // Si on sélectionne une graine, on désélectionne automatiquement l'animal
        if (type != null) {
            this.selectedAnimal = null;
        }
    }

    public CropType getSelectedCrop() {
        return selectedCrop;
    }

    // --- GESTION DES ANIMAUX ---

    /**
     * Définit l'animal sélectionné.
     * Cette méthode règle tes erreurs "cannot find symbol".
     */
    public void setSelectedAnimal(AnimalType type) {
        this.selectedAnimal = type;
        // Si on sélectionne un animal, on désélectionne la graine
        if (type != null) {
            this.selectedCrop = null;
        }
    }

    public AnimalType getSelectedAnimal() {
        return selectedAnimal;
    }

    /**
     * Réinitialise toute la sélection.
     */
    public void clearSelection() {
        this.selectedCrop = null;
        this.selectedAnimal = null;
    }
}