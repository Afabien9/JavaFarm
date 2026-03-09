package main.java.service;

import main.java.model.Animal;
import main.java.model.AnimalType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AnimalService {
    private static AnimalService instance;
    private final ObservableList<Animal> farmAnimals = FXCollections.observableArrayList();

    private AnimalService() {}

    public static AnimalService getInstance() {
        if (instance == null) instance = new AnimalService();
        return instance;
    }

    public boolean buyAnimal(AnimalType type) {
        if (GameService.getInstance().getWallet().spendMoney(type.getBuyPrice())) {
            farmAnimals.add(new Animal(type));
            return true;
        }
        return false;
    }

    public ObservableList<Animal> getFarmAnimals() {
        return farmAnimals;
    }
}