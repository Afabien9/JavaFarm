package main.java.service;

import main.java.model.*;

public class FeedingService {
    private static FeedingService instance;

    public static FeedingService getInstance() {
        if (instance == null) instance = new FeedingService();
        return instance;
    }

    public boolean tryFeed(Enclosure enclosure) {
        if (enclosure.getCurrentAnimal() == null || !enclosure.isHungryProperty().get()) return false;

        Inventory inv = GameService.getInstance().getInventory();
        AnimalType type = enclosure.getCurrentAnimal().getType();
        boolean success = false;

        switch (type) {
            case CHICKEN:
                // Les poules mangent des graines (Wheat)
                success = inv.useSeed(CropType.WHEAT, 1);
                break;
            case SHEEP:
                // Les moutons mangent du Blé récolté
                success = inv.useProduct(CropType.WHEAT, 1);
                break;
            case COW:
                // Les vaches mangent du Maïs récolté
                success = inv.useProduct(CropType.CORN, 1);
                break;
        }

        if (success) {
            enclosure.startProductionAfterFeeding();
            return true;
        }
        return false;
    }
}