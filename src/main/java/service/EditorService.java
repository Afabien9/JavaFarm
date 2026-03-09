package main.java.service;

import javafx.scene.input.KeyCode;

public class EditorService {
    private static EditorService instance;
    private boolean editMode = false;
    private KeyCode selectedTool = null; // P, E, ou T

    private EditorService() {}

    public static EditorService getInstance() {
        if (instance == null) instance = new EditorService();
        return instance;
    }

    public void toggleEditMode(boolean active, KeyCode tool) {
        this.editMode = active;
        this.selectedTool = active ? tool : null;
        System.out.println("Mode Édition : " + (active ? "ON (" + tool + ")" : "OFF"));
    }

    public boolean isEditMode() { return editMode; }
    public KeyCode getSelectedTool() { return selectedTool; }
}