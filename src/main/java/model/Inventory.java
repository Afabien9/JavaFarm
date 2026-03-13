package main.java.model;

import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private final Map<CropType, Integer> seeds = new HashMap<>();
    private final Map<CropType, Integer> products = new HashMap<>();
    private final Map<AnimalType, Integer> animals = new HashMap<>();
    private final Map<String, Integer> animalProducts = new HashMap<>();


    public void addSeed(CropType type, int qty) {
        if (qty > 0) seeds.put(type, seeds.getOrDefault(type, 0) + qty);
    }

    public boolean hasEnoughSeeds(CropType type, int needed) {
        return seeds.getOrDefault(type, 0) >= needed;
    }

    public boolean useSeed(CropType type) {
        return useSeed(type, 1);
    }

    public boolean useSeed(CropType type, int qty) {
        int current = seeds.getOrDefault(type, 0);
        if (current >= qty) {
            seeds.put(type, current - qty);
            return true;
        }
        return false;
    }

    public void addProduct(CropType type, int qty) {
        if (qty > 0) products.put(type, products.getOrDefault(type, 0) + qty);
    }

    public boolean useProduct(CropType type, int qty) {
        int current = products.getOrDefault(type, 0);
        if (current >= qty) {
            products.put(type, current - qty);
            return true;
        }
        return false;
    }


    public void addAnimal(AnimalType type, int qty) {
        if (qty > 0) animals.put(type, animals.getOrDefault(type, 0) + qty);
    }

    public boolean useAnimal(AnimalType type) {
        int current = animals.getOrDefault(type, 0);
        if (current > 0) {
            animals.put(type, current - 1);
            return true;
        }
        return false;
    }


    public void addAnimalProduct(String name, int qty) {
        if (qty > 0) animalProducts.put(name, animalProducts.getOrDefault(name, 0) + qty);
    }

    public Map<String, Integer> getAnimalProducts() { return animalProducts; }
    public void clearAnimalProducts() { animalProducts.clear(); }


    public Map<CropType, Integer> getSeeds() { return seeds; }
    public Map<CropType, Integer> getProducts() { return products; }
    public Map<AnimalType, Integer> getAnimals() { return animals; }
}