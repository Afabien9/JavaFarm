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


            System.out.println("Récolté : " + type.getProduct() + " de " + type.getName());


            GameService.getInstance().getWallet().addMoney(type.getProductValue());
        } else {
            System.out.println("L'animal a trop faim pour produire !");
        }
    }


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