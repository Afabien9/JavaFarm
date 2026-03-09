package main.java.service;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;

public class CameraService {
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    public void makeDraggable(ScrollPane scrollPane) {
        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isSecondaryButtonDown()) { // Clic droit pour déplacer
                mouseAnchorX = event.getSceneX();
                mouseAnchorY = event.getSceneY();
                translateAnchorX = scrollPane.getHvalue();
                translateAnchorY = scrollPane.getVvalue();
            }
        });

        scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.isSecondaryButtonDown()) {
                double deltaX = event.getSceneX() - mouseAnchorX;
                double deltaY = event.getSceneY() - mouseAnchorY;

                // Ajustement de la position du ScrollPane
                scrollPane.setHvalue(translateAnchorX - deltaX / scrollPane.getContent().getBoundsInLocal().getWidth());
                scrollPane.setVvalue(translateAnchorY - deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());
            }
        });
    }
}