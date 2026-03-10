package main.java.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Gère l'argent du joueur et expose une propriété pour la mise à jour automatique de l'UI.
 */
public class Wallet {
    // Solde de départ fixé à 500€
    private final IntegerProperty money = new SimpleIntegerProperty(500);

    public int getMoney() {
        return money.get();
    }

    public IntegerProperty moneyProperty() {
        return money;
    }

    /**
     * Ajoute de l'argent au portefeuille (Récoltes, Ventes, Debug).
     */
    public void addMoney(int amount) {
        if (amount > 0) {
            money.set(money.get() + amount);
        }
    }

    /**
     * Soustrait une somme d'argent.
     * Sécurité ajoutée : empêche le solde de descendre en dessous de 0.
     */
    public void removeMoney(int amount) {
        if (amount > 0) {
            int current = money.get();
            money.set(Math.max(0, current - amount));
        }
    }

    /**
     * Tente de dépenser une somme (Boutique, Améliorations).
     * @param amount Le coût à payer.
     * @return true si le joueur avait assez d'argent et a été débité, false sinon.
     */
    public boolean spendMoney(int amount) {
        if (amount <= 0) return true;

        if (money.get() >= amount) {
            money.set(money.get() - amount);
            return true;
        }

        // Logique optionnelle : On pourrait ajouter un log ici "Fonds insuffisants"
        return false;
    }

    /**
     * Réinitialise le portefeuille (utile pour une nouvelle partie).
     */
    public void reset() {
        money.set(500);
    }
}