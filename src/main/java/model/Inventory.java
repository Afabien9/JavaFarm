package main.java.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Inventory {
    // On stocke des COMPTEURS pour chaque TYPE (Enum)
    private final ObservableMap<CropType, Integer> seeds = FXCollections.observableHashMap();
    private final ObservableMap<CropType, Integer> products = FXCollections.observableHashMap();

    // AJOUTER DES GRAINES (Achat en boutique)
    public void addSeed(CropType type, int qty) {
        if (type == null) return;
        seeds.put(type, seeds.getOrDefault(type, 0) + qty);
    }

    // UTILISER UNE GRAINE (Pour planter)
    // C'est cette méthode qui te manquait (cannot find symbol)
    public boolean useSeed(CropType type) {
        int current = seeds.getOrDefault(type, 0);
        if (current > 0) {
            seeds.put(type, current - 1);
            return true;
        }
        return false;
    }

    // AJOUTER UN PRODUIT (Récolte)
    // CORRECTION : On accepte CropType, PAS Crop
    public void addProduct(CropType type, int qty) {
        if (type == null) return;
        products.put(type, products.getOrDefault(type, 0) + qty);
    }

    // UTILISER UN PRODUIT (Vente)
    public boolean useProduct(CropType type, int qty) {
        int current = products.getOrDefault(type, 0);
        if (current >= qty) {
            products.put(type, current - qty);
            return true;
        }
        return false;
    }

    public ObservableMap<CropType, Integer> getSeeds() { return seeds; }
    public ObservableMap<CropType, Integer> getProducts() { return products; }
}