package main.java.service;

import main.java.model.CropType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SelectionService {
    private static SelectionService instance;
    private final ObjectProperty<CropType> selectedCrop = new SimpleObjectProperty<>(CropType.WHEAT);

    private SelectionService() {}

    public static SelectionService getInstance() {
        if (instance == null) instance = new SelectionService();
        return instance;
    }

    public ObjectProperty<CropType> selectedCropProperty() { return selectedCrop; }
    public void setSelectedCrop(CropType type) { this.selectedCrop.set(type); }
    public CropType getSelectedCrop() { return selectedCrop.get(); }

    // ON RAJOUTE LA MÉTHODE ICI
    public void selectCropByName(String cropName) {
        for (CropType type : CropType.values()) {
            // On compare le nom (Blé, Maïs, Tomate) avec ce qui est sélectionné
            if (type.getName().equalsIgnoreCase(cropName)) {
                setSelectedCrop(type);
                System.out.println("[DEBUG] Sélection changée pour : " + type.getName());
                break;
            }
        }
    }
}