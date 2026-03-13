package main.java.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;
import main.java.model.*;
import java.util.ArrayList;
import java.util.List;

public class GameService {
    private static GameService instance;

    private Inventory inventory;
    private Wallet wallet;

    private final List<Plot> allPlots = new ArrayList<>();
    private final List<Enclosure> allEnclosures = new ArrayList<>();

    private final BooleanProperty debugMode = new SimpleBooleanProperty(false);
    private long lastToggleTime = 0;

    private final int BASE_PLOT_PRICE = 50;
    private final int BASE_ENCLOSURE_PRICE = 150;
    private final int PLOT_PRICE_INCREMENT = 50;

    private GameService() {
        startGlobalTimer();
    }

    public static GameService getInstance() {
        if (instance == null) instance = new GameService();
        return instance;
    }

    private void startGlobalTimer() {
        Timeline gameLoop = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            updateAnimalsProduction();
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void updateAnimalsProduction() {
        for (Enclosure enc : allEnclosures) {
            Animal animal = enc.getCurrentAnimal();
            if (animal != null) {
                animal.updateStatus();
                if (animal.isReadyToProduce()) {
                    enc.stateProperty().set(PlotState.READY);
                }
            }
        }
    }

    public void registerEnclosure(Enclosure enc) {
        if (!allEnclosures.contains(enc)) allEnclosures.add(enc);
    }

    public void unregisterEnclosure(Enclosure enc) {
        allEnclosures.remove(enc);
    }

    public void setInventory(Inventory i) { this.inventory = i; }
    public void setWallet(Wallet w) { this.wallet = w; }
    public Wallet getWallet() { return wallet; }
    public Inventory getInventory() { return inventory; }

    public int getNextPlotPrice(int currentCount) {
        return BASE_PLOT_PRICE + (currentCount * PLOT_PRICE_INCREMENT);
    }

    public int getNextEnclosurePrice(int currentCount) {
        return (int) (BASE_ENCLOSURE_PRICE * Math.pow(1.5, currentCount));
    }

    public void refundStructure(boolean isPlot, int countAfterDestruction) {
        int originalPrice = isPlot ? getNextPlotPrice(countAfterDestruction) : getNextEnclosurePrice(countAfterDestruction);
        int refund = originalPrice / 2;
        if (wallet != null) wallet.addMoney(refund);
    }

    public boolean buy(Object selected) {
        if (selected instanceof CropType) {
            CropType type = (CropType) selected;
            if (wallet.spendMoney(type.getBuyPrice())) {
                inventory.addSeed(type, 1);
                return true;
            }
        } else if (selected instanceof AnimalType) {
            AnimalType type = (AnimalType) selected;
            if (wallet.spendMoney(type.getBuyPrice())) {
                inventory.addAnimal(type, 1);
                return true;
            }
        }
        return false;
    }

    public void plantCrop(Plot plot, CropType type) {
        if (plot != null && type != null) {
            plot.plant(type);
            if (!allPlots.contains(plot)) allPlots.add(plot);
        }
    }

    public void harvestPlot(Plot plot) {
        if (plot != null && plot.getState() == PlotState.READY && plot.getCurrentCrop() != null) {
            CropType type = plot.getCurrentCrop().getType();
            if (inventory != null) {
                inventory.addProduct(type, 1);
                LevelService.getInstance().addXP(20);
            }
            plot.harvest();
        }
    }

    public void collectFromEnclosure(Enclosure enc) {
        Animal animal = enc.getCurrentAnimal();
        if (enc != null && enc.getState() == PlotState.READY && animal != null) {
            String product = animal.getProducedProductName();
            if (inventory != null) {
                inventory.addAnimalProduct(product, 1);
                int xpGain = 40 + (animal.getType().getRequiredLevel() * 10);
                LevelService.getInstance().addXP(xpGain);
                enc.collectProduct();
            }
        }
    }

    public void toggleDebug() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToggleTime < 300) return;
        lastToggleTime = currentTime;
        debugMode.set(!debugMode.get());
    }

    public BooleanProperty debugModeProperty() { return debugMode; }
    public boolean isDebugActive() { return debugMode.get(); }
    public List<Plot> getAllPlots() { return allPlots; }
}