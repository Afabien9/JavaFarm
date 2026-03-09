package main.java.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import main.java.model.WeatherType; // Import de l'énumération
import java.util.Random;

public class WeatherManager {
    private WeatherType currentWeather = WeatherType.SUNNY;
    private final MainController mainCtrl;
    private final Random random = new Random();

    public WeatherManager(MainController controller, double intervalMs) {
        this.mainCtrl = controller;
        startWeatherCycle(intervalMs);
    }

    private void startWeatherCycle(double intervalMs) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(intervalMs), e -> {
            changeWeatherRandomly();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void changeWeatherRandomly() {
        WeatherType[] types = WeatherType.values();
        this.currentWeather = types[random.nextInt(types.length)];
        if (mainCtrl != null) {
            // On envoie l'objet WeatherType complet au lieu d'un String
            mainCtrl.updateWeatherUI(String.valueOf(currentWeather));
        }
    }

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }
}