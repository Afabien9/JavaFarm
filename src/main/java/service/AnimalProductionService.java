package main.java.service;

import main.java.model.Animal;
import main.java.model.AnimalType;
import main.java.model.Inventory;
import main.java.model.Wallet;

public class AnimalProductionService {
    private static AnimalProductionService instance;

    private AnimalProductionService() {}

    public static AnimalProductionService getInstance() {
        if (instance == null) instance = new AnimalProductionService();
        return instance;
    }

    public void collectResource(Animal animal) {
        if (!animal.isHungryProperty().get()) {
            AnimalType type = animal.getType();
            Inventory inventory = GameService.getInstance().getInventory();

            // On ajoute le produit (Lait, Oeuf, etc.) à l'inventaire
            // Note : Tu peux créer des types de produits spécifiques dans ton Inventory plus tard
            System.out.println("Récolté : " + type.getProduct() + " de " + type.getName());

            // Gain immédiat ou stockage selon ton choix (ici gain immédiat pour simplifier)
            GameService.getInstance().getWallet().addMoney(type.getProductValue());
        } else {
            System.out.println("L'animal a trop faim pour produire !");
        }
    }

    /**
     * Nourrit l'animal en consommant de l'argent (coût de nourriture).
     */
    public boolean feedAnimal(Animal animal) {
        Wallet wallet = GameService.getInstance().getWallet();
        int cost = animal.getType().getFoodCost();

        if (wallet.spendMoney(cost)) {
            animal.feed();
            System.out.println(animal.getType().getName() + " a été nourri.");
            return true;
        }
        return false;
    }
}