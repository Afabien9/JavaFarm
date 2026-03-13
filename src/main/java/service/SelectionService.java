package main.java.service;

import main.java.model.AnimalType;
import main.java.model.CropType;


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



    public void setSelectedCrop(CropType type) {
        this.selectedCrop = type;

        if (type != null) {
            this.selectedAnimal = null;
        }
    }

    public CropType getSelectedCrop() {
        return selectedCrop;
    }


    public void setSelectedAnimal(AnimalType type) {
        this.selectedAnimal = type;

        if (type != null) {
            this.selectedCrop = null;
        }
    }

    public AnimalType getSelectedAnimal() {
        return selectedAnimal;
    }


    public void clearSelection() {
        this.selectedCrop = null;
        this.selectedAnimal = null;
    }
}