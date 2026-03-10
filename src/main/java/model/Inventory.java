package main.java.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Gère le stockage global de la ferme.
 * Centralise les graines achetées, les récoltes, les animaux en attente de placement
 * et les produits dérivés (œufs, lait, etc.).
 */
public class Inventory {

    // Utilisation de Maps pour associer un Type à une Quantité
    private final Map<CropType, Integer> seeds = new HashMap<>();
    private final Map<CropType, Integer> products = new HashMap<>();
    private final Map<AnimalType, Integer> animals = new HashMap<>();
    private final Map<String, Integer> animalProducts = new HashMap<>();

    // --- SECTION : GRAINES (SEEDS) ---

    /**
     * Ajoute des graines au stock après un achat.
     */
    public void addSeed(CropType type, int qty) {
        if (qty > 0) {
            seeds.put(type, seeds.getOrDefault(type, 0) + qty);
        }
    }

    /**
     * Vérifie si le joueur possède assez de graines.
     */
    public boolean hasEnoughSeeds(CropType type, int needed) {
        return seeds.getOrDefault(type, 0) >= needed;
    }

    /**
     * Consomme une seule graine (alias pour la plantation standard).
     */
    public boolean useSeed(CropType type) {
        return useSeed(type, 1);
    }

    /**
     * Consomme une quantité précise de graines.
     * Retourne true si l'opération a réussi.
     */
    public boolean useSeed(CropType type, int qty) {
        int current = seeds.getOrDefault(type, 0);
        if (current >= qty) {
            seeds.put(type, current - qty);
            return true;
        }
        return false;
    }

    /**
     * Supprime des graines sans vérification de succès (méthode utilitaire).
     */
    public void removeSeeds(CropType type, int qty) {
        int current = seeds.getOrDefault(type, 0);
        if (current >= qty) {
            seeds.put(type, current - qty);
        }
    }

    // --- SECTION : PRODUITS VÉGÉTAUX (RECOLTES) ---

    /**
     * Ajoute des légumes/céréales récoltés dans la grange.
     */
    public void addProduct(CropType type, int qty) {
        if (qty > 0) {
            products.put(type, products.getOrDefault(type, 0) + qty);
        }
    }

    /**
     * Retire des produits (pour la vente ou transformation).
     */
    public boolean useProduct(CropType type, int qty) {
        int current = products.getOrDefault(type, 0);
        if (current >= qty) {
            products.put(type, current - qty);
            return true;
        }
        return false;
    }

    // --- SECTION : ANIMAUX (STOCK AVANT PLACEMENT) ---

    /**
     * Ajoute un animal acheté au stock (avant qu'il ne soit placé dans un enclos).
     */
    public void addAnimal(AnimalType type, int qty) {
        if (qty > 0) {
            animals.put(type, animals.getOrDefault(type, 0) + qty);
        }
    }

    /**
     * Retire un animal du stock pour le placer sur la carte.
     */
    public boolean useAnimal(AnimalType type) {
        int current = animals.getOrDefault(type, 0);
        if (current > 0) {
            animals.put(type, current - 1);
            return true;
        }
        return false;
    }

    // --- SECTION : PRODUITS ANIMAUX (LAIT, OEUFS, ETC.) ---

    /**
     * Ajoute un produit issu d'un animal.
     */
    public void addAnimalProduct(String name, int qty) {
        if (qty > 0) {
            animalProducts.put(name, animalProducts.getOrDefault(name, 0) + qty);
        }
    }

    /**
     * Récupère la map complète des produits animaux pour l'affichage/vente.
     */
    public Map<String, Integer> getAnimalProducts() {
        return animalProducts;
    }

    /**
     * Vide le stock de produits animaux (après une vente totale par exemple).
     */
    public void clearAnimalProducts() {
        animalProducts.clear();
    }

    // --- GETTERS ---

    public Map<CropType, Integer> getSeeds() {
        return seeds;
    }

    public Map<CropType, Integer> getProducts() {
        return products;
    }

    public Map<AnimalType, Integer> getAnimals() {
        return animals;
    }
}