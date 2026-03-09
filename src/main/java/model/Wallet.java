package main.java.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Gère l'argent du joueur.
 */
public class Wallet {
    private final IntegerProperty money = new SimpleIntegerProperty(500); // Solde de départ

    public int getMoney() {
        return money.get();
    }

    public IntegerProperty moneyProperty() {
        return money;
    }

    /**
     * Ajoute de l'argent au portefeuille.
     */
    public void addMoney(int amount) {
        if (amount > 0) {
            money.set(money.get() + amount);
        }
    }

    /**
     * MÉTHODE REQUISE : Soustrait directement de l'argent.
     * Utilisée par le GameService pour les achats d'animaux.
     */
    public void removeMoney(int amount) {
        if (amount > 0) {
            money.set(money.get() - amount);
        }
    }

    /**
     * Tente de dépenser une somme.
     * Retourne true si le solde est suffisant, false sinon.
     */
    public boolean spendMoney(int amount) {
        if (money.get() >= amount) {
            money.set(money.get() - amount);
            return true;
        }
        return false;
    }
}