package main.java.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import main.java.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service central (Singleton).
 * Correction : Ajout d'un anti-rebond pour éviter le double basculement du mode debug.
 */
public class GameService {
    private static GameService instance;

    private Inventory inventory;
    private Wallet wallet;
    private final List<Plot> allPlots = new ArrayList<>();
    private final List<Enclosure> allEnclosures = new ArrayList<>();
    private final BooleanProperty debugMode = new SimpleBooleanProperty(false);

    // Variable pour empêcher le double déclenchement rapide
    private long lastToggleTime = 0;

    private GameService() {
        this.inventory = new Inventory();
        this.wallet = new Wallet();
    }

    public static GameService getInstance() {
        if (instance == null) instance = new GameService();
        return instance;
    }

    public void setInventory(Inventory i) { this.inventory = i; }
    public void setWallet(Wallet w) { this.wallet = w; }
    public Wallet getWallet() { return wallet; }
    public Inventory getInventory() { return inventory; }

    /**
     * Boutique : On garde wallet.spendMoney.
     */
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

    /**
     * Plantation : Utilise 1 seconde si le mode debug est actif.
     */
    public void plantCrop(Plot plot, CropType type) {
        if (plot != null && type != null) {
            // FIX DEBUG : Si debug actif, temps = 1s, sinon temps normal du type
            int effectiveGrowthTime = isDebugActive() ? 1 : type.getGrowthTime();

            Crop newPlant = new Crop(
                    type.getName(),
                    type.getBuyPrice(),
                    (int)(type.getBuyPrice() * 1.5),
                    effectiveGrowthTime
            );

            plot.plant(newPlant);
            if (!allPlots.contains(plot)) allPlots.add(plot);
        }
    }

    public void harvestPlot(Plot plot) {
        if (plot != null && plot.getState() == PlotState.READY && plot.getCurrentCrop() != null) {
            for (CropType type : CropType.values()) {
                if (type.getName().equals(plot.getCurrentCrop().getName())) {
                    inventory.addProduct(type, 1);
                    break;
                }
            }
            plot.harvest();
        }
    }

    /**
     * Placement animal : Applique aussi le temps réduit si debug actif.
     */
    public void placeAnimalFromInventory(Enclosure enc, AnimalType type) {
        if (enc != null && type != null) {
            enc.addAnimal(type); // L'enclos doit vérifier GameService.getInstance().isDebugActive() pour son timer interne
            if (!allEnclosures.contains(enc)) allEnclosures.add(enc);
        }
    }

    public void collectFromEnclosure(Enclosure enc) {
        if (enc != null && enc.getState() == PlotState.READY) {
            AnimalType type = enc.getCurrentAnimal();
            if (type != null && inventory != null) {
                inventory.addAnimalProduct(type.getProduct(), 1);
                enc.collectProduct();
            }
        }
    }

    public void buyAnimal(Enclosure enc, AnimalType selectedAnimal) {
        if (enc != null && selectedAnimal != null && enc.getState() == PlotState.EMPTY) {
            if (wallet.spendMoney(selectedAnimal.getBuyPrice())) {
                enc.addAnimal(selectedAnimal);
                if (!allEnclosures.contains(enc)) allEnclosures.add(enc);
            }
        }
    }

    public BooleanProperty debugModeProperty() { return debugMode; }
    public boolean isDebugActive() { return debugMode.get(); }

    /**
     * Bascule le mode debug avec une sécurité anti-rebond.
     */
    public void toggleDebug() {
        long currentTime = System.currentTimeMillis();

        // Si le dernier clic a eu lieu il y a moins de 300ms, on ignore
        if (currentTime - lastToggleTime < 300) {
            return;
        }

        lastToggleTime = currentTime;
        debugMode.set(!debugMode.get());
        System.out.println("Debug Mode: " + debugMode.get());
    }

    public List<Plot> getAllPlots() { return allPlots; }
    public List<Enclosure> getAllEnclosures() { return allEnclosures; }
}