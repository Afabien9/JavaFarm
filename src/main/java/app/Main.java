package main.java.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controller.MainController;
import main.java.model.SaveManager;

public class Main extends Application {

    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        this.mainController = loader.getController();
        scene.setOnKeyPressed(mainController::handleKeyPress);

        if (mainController != null && mainController.getMapController() != null) {
            SaveManager.loadGame(
                    mainController.getWallet(),
                    mainController.getInventory(),
                    mainController.getMapController()
            );
            mainController.refreshInventoryUI();
        }

        primaryStage.setTitle("Farm My Farm - Gestion Agricole");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (mainController != null && mainController.getMapController() != null) {
            SaveManager.saveGame(
                    mainController.getWallet(),
                    mainController.getInventory(),
                    mainController.getMapController()
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}