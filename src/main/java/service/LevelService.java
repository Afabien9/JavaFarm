package main.java.service;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import main.java.model.CropType;

/**
 * Service gérant la progression du joueur et le déblocage du contenu.
 */
public class LevelService {
    private static LevelService instance;

    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    private final IntegerProperty currentXP = new SimpleIntegerProperty(0);
    private final IntegerProperty xpToNextLevel = new SimpleIntegerProperty(100);

    public static LevelService getInstance() {
        if (instance == null) instance = new LevelService();
        return instance;
    }

    public void addXP(int amount) {
        if (amount <= 0) return;
        currentXP.set(currentXP.get() + amount);
        System.out.println("[LEVEL] XP ajoutée : " + amount + " | Total : " + currentXP.get() + "/" + xpToNextLevel.get());
        checkLevelUp();
    }


    public int getMaxPlots() {
        return 2 + (getCurrentLevel() - 1) * 2;
    }

    public int getMaxEnclosures() {
        return 1 + (getCurrentLevel() - 1);
    }

    private void checkLevelUp() {
        while (currentXP.get() >= xpToNextLevel.get()) {
            currentXP.set(currentXP.get() - xpToNextLevel.get());
            currentLevel.set(currentLevel.get() + 1);

            int nextThreshold = (int) (xpToNextLevel.get() * 1.5);
            xpToNextLevel.set(nextThreshold);

            System.out.println("[LEVEL] FÉLICITATIONS ! Niveau " + currentLevel.get() + " atteint !");

            // --- ANNONCE DES DÉBLOCAGES ---
            for (CropType type : CropType.values()) {
                if (type.getMinLevel() == currentLevel.get()) {
                    System.out.println("[DÉBLOCAGE] La graine '" + type.getName() + "' est maintenant disponible !");
                }
            }

            // Bonus financier
            if (GameService.getInstance().getWallet() != null) {
                GameService.getInstance().getWallet().addMoney(100);
            }
        }
    }

    public int getCurrentLevel() { return currentLevel.get(); }
    public IntegerProperty currentLevelProperty() { return currentLevel; }
    public int getCurrentXP() { return currentXP.get(); }
    public IntegerProperty currentXPProperty() { return currentXP; }
    public int getXpToNextLevel() { return xpToNextLevel.get(); }
    public IntegerProperty xpToNextLevelProperty() { return xpToNextLevel; }
}