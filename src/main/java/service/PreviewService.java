package main.java.service;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PreviewService {
    private static PreviewService instance;
    private Rectangle ghost;

    // CONFIGURATION DE LA GRILLE (Identique au PlacementService)
    private final int GRID_SIZE = 80;
    private final int GAP = 10;
    private final int CELL_TOTAL = GRID_SIZE + GAP; // 90px

    private PreviewService() {}

    public static PreviewService getInstance() {
        if (instance == null) instance = new PreviewService();
        return instance;
    }

    /**
     * Met à jour ou affiche le fantôme de prévisualisation.
     */
    public void updatePreview(Pane gameWorld, double x, double y, boolean active) {
        // 1. Si on quitte le mode construction, on retire le fantôme proprement
        if (!active) {
            if (ghost != null) {
                gameWorld.getChildren().remove(ghost);
                ghost = null;
            }
            return;
        }

        // 2. Création du fantôme s'il n'existe pas encore
        if (ghost == null) {
            ghost = new Rectangle(GRID_SIZE, GRID_SIZE);
            ghost.setFill(Color.web("#f1c40f", 0.4)); // Jaune semi-transparent
            ghost.setStroke(Color.WHITE);
            ghost.setStrokeWidth(2);

            // --- LIGNE CRUCIALE ---
            // Rend le fantôme "invisible" pour la souris et le CollisionService.
            // Cela permet de cliquer "à travers" lui pour poser l'objet réel.
            ghost.setMouseTransparent(true);

            gameWorld.getChildren().add(ghost);
        }

        // 3. Calcul de l'alignement sur la grille (Snap-to-Grid)
        double snapX = Math.floor(x / CELL_TOTAL) * CELL_TOTAL;
        double snapY = Math.floor(y / CELL_TOTAL) * CELL_TOTAL;

        ghost.setX(snapX);
        ghost.setY(snapY);
    }
}