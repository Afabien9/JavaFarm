package main.java.model;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Animal {
    private final AnimalType type;
    private final BooleanProperty isHungry = new SimpleBooleanProperty(false);
    private long lastFedTime;

    public Animal(AnimalType type) {
        this.type = type;
        this.lastFedTime = System.currentTimeMillis();
    }

    public void checkHunger(long interval) {
        if (System.currentTimeMillis() - lastFedTime > interval) {
            isHungry.set(true);
        }
    }

    public void feed() {
        lastFedTime = System.currentTimeMillis();
        isHungry.set(false);
    }

    public AnimalType getType() { return type; }
    public BooleanProperty isHungryProperty() { return isHungry; }
}