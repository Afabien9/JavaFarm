package main.java.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Wallet {
    private final IntegerProperty money = new SimpleIntegerProperty(500);

    public IntegerProperty moneyProperty() { return money; }

    public int getMoney() { return money.get(); }

    public void addMoney(int amount) {
        money.set(money.get() + amount);
    }

    public boolean spendMoney(int amount) {
        if (money.get() >= amount) {
            money.set(money.get() - amount);
            return true;
        }
        return false;
    }

    /**
     * Retourne le montant actuel sous forme de texte.
     */
    public String getBalance() {
        // On transforme le int en String.
        // Tu peux aussi ajouter le symbole € ici directement si tu veux.
        return String.valueOf(money.get());
    }
}