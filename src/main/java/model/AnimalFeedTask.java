package main.java.model;

import main.java.service.GameService;

public class AnimalFeedTask {

    public static boolean tryFeedAnimal(Enclosure enclosure) {
        AnimalType type = enclosure.getCurrentAnimal().getType();
        Inventory inventory = GameService.getInstance().getInventory();

        if (type == null) return false;

        switch (type) {
            case CHICKEN:
                if (inventory.useSeed(CropType.WHEAT, 1)) return true;
                break;
            case COW:
                if (inventory.useProduct(CropType.CORN, 1)) return true;
                break;
            case SHEEP:
                if (inventory.useProduct(CropType.WHEAT, 1)) return true;
                break;
        }
        return false;
    }
}