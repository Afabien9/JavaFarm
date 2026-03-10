package main.java.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import main.java.model.UpgradeType;

public class UpgradeService {
    private static UpgradeService instance;
    private final ObservableSet<UpgradeType> purchasedUpgrades = FXCollections.observableSet();

    public static UpgradeService getInstance() {
        if (instance == null) instance = new UpgradeService();
        return instance;
    }

    // MÉTHODE MANQUANTE AJOUTÉE ICI
    public boolean hasUpgrade(UpgradeType upgrade) {
        return purchasedUpgrades.contains(upgrade);
    }

    public boolean buyUpgrade(UpgradeType upgrade) {
        if (hasUpgrade(upgrade)) return false;

        // Vérification du portefeuille via GameService
        if (GameService.getInstance().getWallet().spendMoney(upgrade.getPrice())) {
            purchasedUpgrades.add(upgrade);
            return true;
        }
        return false;
    }

    public double getModifier(UpgradeType type) {
        return hasUpgrade(type) ? type.getMultiplier() : 1.0;
    }
}