package main.java.app;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import main.java.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.model.SaveManager;
import main.java.service.CameraService;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Scene scene = new Scene(loader.load());


        MainController controller = loader.getController();
        scene.setOnKeyPressed(controller::handleKeyPress);


        SaveManager.loadGame();

        primaryStage.setTitle("Farm My Farm - Gestion Agricole");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {

        SaveManager.saveGame();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private ScrollPane mapScroll;
    private final CameraService cameraService = new CameraService();

    public void initialize() {
        cameraService.makeDraggable(mapScroll);
    }
}