package main.java.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Wallet {

    private final IntegerProperty money = new SimpleIntegerProperty(500);

    public int getMoney() {
        return money.get();
    }

    public IntegerProperty moneyProperty() {
        return money;
    }


    public void addMoney(int amount) {
        if (amount > 0) {
            money.set(money.get() + amount);
        }
    }


    public void removeMoney(int amount) {
        if (amount > 0) {
            int current = money.get();
            money.set(Math.max(0, current - amount));
        }
    }



    public boolean spendMoney(int amount) {
        if (amount <= 0) return true;

        if (money.get() >= amount) {
            money.set(money.get() - amount);
            return true;
        }


        return false;
    }


    public void reset() {
        money.set(500);
    }
}