package main.java.service;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

/**
 * Gère la logique de décision lors du clic sur la carte en fonction du mode actif.
 */
public class ConstructionManager {
    private static ConstructionManager instance;

    private ConstructionManager() {}

    public static ConstructionManager getInstance() {
        if (instance == null) {
            instance = new ConstructionManager();
        }
        return instance;
    }

    /**
     * Exécute l'action de construction appropriée selon la touche maintenue.
     * * @param code Le KeyCode de la touche pressée (P, E ou T)
     * @param gameWorld Le panneau (Pane) où l'objet doit être ajouté
     * @param x Coordonnée X du clic
     * @param y Coordonnée Y du clic
     */
    public void executeConstruction(KeyCode code, Pane gameWorld, double x, double y) {
        if (code == null) return;

        switch (code) {
            case P:
                // ACHAT PARCELLE (100€) - Aligné sur la grille de 80px
                PlacementService.getInstance().buyAndPlacePlot(gameWorld, x, y);
                break;

            case E:
                // ACHAT ENCLOS (500€)
                PlacementService.getInstance().buyAndPlaceEnclosure(gameWorld, x, y);
                break;

            case T:
                // ACHAT TERRAIN / EXPANSION (500€)
                LandService.getInstance().buyExpansion(gameWorld, x, y);
                break;

            default:
                // Aucune action pour les autres touches
                break;
        }
    }
}