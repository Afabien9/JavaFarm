package main.java.model;

import main.java.service.GameService;

public class AnimalFeedTask {
    /**
     * Vérifie si le joueur possède la nourriture requise et nourrit l'animal.
     * @param enclosure L'enclos contenant l'animal à nourrir.
     * @return true si l'animal a été nourri, false sinon.
     */
    public static boolean tryFeedAnimal(Enclosure enclosure) {
        AnimalType type = enclosure.getCurrentAnimal();
        Inventory inventory = GameService.getInstance().getInventory();

        if (type == null) return false;

        switch (type) {
            case CHICKEN: // Les poules mangent des graines (Wheat par exemple)
                if (inventory.useSeed(CropType.WHEAT)) return true;
                break;
            case COW: // Les vaches mangent du maïs récolté
                if (inventory.useProduct(CropType.CORN, 1)) return true;
                break;
            case SHEEP: // Les moutons mangent du blé récolté
                if (inventory.useProduct(CropType.WHEAT, 1)) return true;
                break;
        }
        return false;
    }
}