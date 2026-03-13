package main.java.service;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.java.model.Plot;

public class LandService {
    private static LandService instance;
    private final int LAND_PRICE = 500; // Prix pour agrandir le terrain

    private LandService() {}

    public static LandService getInstance() {
        if (instance == null) instance = new LandService();
        return instance;
    }


    public void buyExpansion(Pane gameWorld, double x, double y) {
        if (GameService.getInstance().getWallet().spendMoney(LAND_PRICE)) {

            Rectangle land = new Rectangle(300, 300);
            land.setX(x - 150);
            land.setY(y - 150);
            land.setFill(Color.web("#7ed6df"));
            land.setStroke(Color.WHITE);
            land.setStrokeWidth(2);
            land.setOpacity(0.4);


            gameWorld.getChildren().add(0, land);
            System.out.println("Nouveau terrain acheté !");
        } else {
            System.out.println("Pas assez d'argent (500€ requis).");
        }
    }
}