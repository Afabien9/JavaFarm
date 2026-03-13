package main.java.service;

import main.java.model.CropType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SelectionManager {
    private static SelectionManager instance;


    private final ObjectProperty<CropType> selectedCrop = new SimpleObjectProperty<>(CropType.WHEAT);

    private SelectionManager() {}

    public static SelectionManager getInstance() {
        if (instance == null) {
            instance = new SelectionManager();
        }
        return instance;
    }

    public ObjectProperty<CropType> selectedCropProperty() {
        return selectedCrop;
    }

    public void setSelectedCrop(CropType type) {
        this.selectedCrop.set(type);
    }

    public CropType getSelectedCrop() {
        return selectedCrop.get();
    }
}
