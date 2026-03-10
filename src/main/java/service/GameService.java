package main.java.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    private final int PLOT_PRICE = 50;
    private final int ENCLOSURE_PRICE = 150;

    private GameService() {}

    public static GameService getInstance() {
        if (instance == null) instance = new GameService();
        return instance;
    }

    public void setInventory(Inventory i) { this.inventory = i; }
    public void setWallet(Wallet w) { this.wallet = w; }
    public Wallet getWallet() { return wallet; }
    public Inventory getInventory() { return inventory; }

    public int getPlotPrice() { return PLOT_PRICE; }
    public int getEnclosurePrice() { return ENCLOSURE_PRICE; }

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

    public void refundStructure(boolean isPlot) {
        int refund = isPlot ? PLOT_PRICE / 2 : ENCLOSURE_PRICE / 2;
        if (wallet != null) {
            wallet.addMoney(refund);
            System.out.println("[ECONOMY] Remboursement de " + refund + " € (Destruction).");
        }
    }

    public void plantCrop(Plot plot, CropType type) {
        if (plot != null && type != null) {
            plot.plant(type);
            if (!allPlots.contains(plot)) allPlots.add(plot);
        }
    }

    public void harvestPlot(Plot plot) {
        this.harvestCrop(plot);
    }

    public void harvestCrop(Plot plot) {
        if (plot != null && plot.getState() == PlotState.READY && plot.getCurrentCrop() != null) {
            CropType type = plot.getCurrentCrop().getType();
            if (inventory != null) {
                inventory.addProduct(type, 1);
                LevelService.getInstance().addXP(20);
                System.out.println("[GAME] Récolte de " + type.getName() + " effectuée. +20 XP");
            }
            plot.harvest();
        }
    }

    public void collectFromEnclosure(Enclosure enc) {
        if (enc != null && enc.getState() == PlotState.READY) {
            AnimalType type = enc.getCurrentAnimal();
            if (type != null && inventory != null) {
                inventory.addAnimalProduct(type.getProduct(), 1);
                LevelService.getInstance().addXP(40);
                System.out.println("[GAME] Collecte Animale (" + type.getName() + ") effectuée. +40 XP");
                enc.collectProduct();
            }
        }
    }

    public void toggleDebug() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToggleTime < 300) return;
        lastToggleTime = currentTime;
        debugMode.set(!debugMode.get());
        System.out.println("[DEBUG] Mode Debug : " + debugMode.get());
    }

    public BooleanProperty debugModeProperty() { return debugMode; }
    public boolean isDebugActive() { return debugMode.get(); }
    public List<Plot> getAllPlots() { return allPlots; }
    public List<Enclosure> getAllEnclosures() { return allEnclosures; }
}