package main.java.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Gère le stockage global.
 * Les méthodes sont nommées pour être explicites et éviter les erreurs de compilation.
 */
public class Inventory {
    private final Map<CropType, Integer> seeds = new HashMap<>();
    private final Map<CropType, Integer> products = new HashMap<>();
    private final Map<AnimalType, Integer> animals = new HashMap<>();
    private final Map<String, Integer> animalProducts = new HashMap<>();

    // --- GRAINES ---
    public void addSeed(CropType type, int qty) {
        seeds.put(type, seeds.getOrDefault(type, 0) + qty);
    }

    public boolean hasEnoughSeeds(CropType type, int needed) {
        return seeds.getOrDefault(type, 0) >= needed;
    }

    /**
     * MÉTHODE RESTAURÉE : Pour assurer la compatibilité avec MapController
     * et les autres classes qui utilisent encore l'appel à 1 graine.
     */
    public boolean useSeed(CropType type) {
        return useSeed(type, 1);
    }

    /**
     * Version surchargée pour consommer plusieurs graines d'un coup.
     */
    public boolean useSeed(CropType type, int qty) {
        int current = seeds.getOrDefault(type, 0);
        if (current >= qty) {
            seeds.put(type, current - qty);
            return true;
        }
        return false;
    }

    public void removeSeeds(CropType type, int qty) {
        int current = seeds.getOrDefault(type, 0);
        if (current >= qty) {
            seeds.put(type, current - qty);
        }
    }

    // --- PRODUITS VÉGÉTAUX ---
    public void addProduct(CropType type, int qty) {
        products.put(type, products.getOrDefault(type, 0) + qty);
    }

    public boolean useProduct(CropType type, int qty) {
        int current = products.getOrDefault(type, 0);
        if (current >= qty) {
            products.put(type, current - qty);
            return true;
        }
        return false;
    }

    // --- ANIMAUX (STOCK) ---
    public void addAnimal(AnimalType type, int qty) {
        animals.put(type, animals.getOrDefault(type, 0) + qty);
    }

    public boolean useAnimal(AnimalType type) {
        int qty = animals.getOrDefault(type, 0);
        if (qty > 0) {
            animals.put(type, qty - 1);
            return true;
        }
        return false;
    }

    // --- PRODUITS ANIMAUX (Lait, Oeuf, etc.) ---
    public void addAnimalProduct(String name, int qty) {
        animalProducts.put(name, animalProducts.getOrDefault(name, 0) + qty);
    }

    public Map<String, Integer> getAnimalProducts() { return animalProducts; }
    public void clearAnimalProducts() { animalProducts.clear(); }

    // --- GETTERS ---
    public Map<CropType, Integer> getSeeds() { return seeds; }
    public Map<CropType, Integer> getProducts() { return products; }
    public Map<AnimalType, Integer> getAnimals() { return animals; }
}