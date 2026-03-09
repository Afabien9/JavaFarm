package main.java.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import main.java.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SERVICE CENTRAL COMPLET.
 * Ne supprime plus de méthodes à l'intérieur !
 */
public class GameService {
    private static GameService instance;

    private Inventory inventory;
    private Wallet wallet;
    private final List<Plot> allPlots = new ArrayList<>();

    // Propriété réactive pour le Debug
    private final BooleanProperty debugMode = new SimpleBooleanProperty(false);

    private GameService() {
        this.inventory = new Inventory();
        this.wallet = new Wallet();
    }

    public static GameService getInstance() {
        if (instance == null) instance = new GameService();
        return instance;
    }

    // --- LIAISON DONNÉES ---
    public void setInventory(Inventory inventory) { this.inventory = inventory; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
    public Inventory getInventory() { return inventory; }
    public Wallet getWallet() { return wallet; }
    public List<Plot> getAllPlots() { return allPlots; }

    // --- LOGIQUE DEBUG ---
    public BooleanProperty debugModeProperty() { return debugMode; }
    public boolean isDebugActive() { return debugMode.get(); }
    public void toggleDebug() {
        debugMode.set(!debugMode.get());
        System.out.println("DEBUG : Mode " + (isDebugActive() ? "ON" : "OFF"));
    }

    // --- LOGIQUE MÉTIER ---

    public void plantCrop(Plot plot, CropType type) {
        if (plot != null && type != null) {
            Crop newPlant = new Crop(
                    type.getName(),
                    type.getBuyPrice(),
                    (int)(type.getBuyPrice() * 1.5),
                    type.getGrowthTime()
            );
            plot.plant(newPlant);
            if (!allPlots.contains(plot)) allPlots.add(plot);
        }
    }

    /**
     * RÉPARÉ : La méthode qui te manquait.
     */
    public void harvestPlot(Plot plot) {
        if (plot != null && plot.getState() == PlotState.READY && inventory != null) {
            String cropName = plot.getCurrentCrop().getName();
            CropType type = getCropTypeFromName(cropName);

            if (type != null) {
                inventory.addProduct(type, 1);
                plot.harvest();
                System.out.println("RÉCOLTE : " + type.getName() + " ajouté à l'inventaire.");
            }
        }
    }

    private CropType getCropTypeFromName(String name) {
        for (CropType t : CropType.values()) {
            if (t.getName().equalsIgnoreCase(name)) return t;
        }
        return null;
    }
}