package main.java.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Animal {
    private final AnimalType type;
    private final BooleanProperty isHungry = new SimpleBooleanProperty(true);
    private final BooleanProperty isReadyToProduce = new SimpleBooleanProperty(false);
    private long lastFedTime;

    public Animal(AnimalType type) {
        this.type = type;
    }

    public void feed() {
        this.lastFedTime = System.currentTimeMillis();
        this.isHungry.set(false);
        this.isReadyToProduce.set(false);
    }

    public boolean isHungry() {
        return isHungry.get();
    }

    public boolean isReadyToProduce() {
        updateStatus();
        return isReadyToProduce.get();
    }

    public void updateStatus() {
        if (isHungry.get() || isReadyToProduce.get()) {
            return;
        }

        long elapsed = System.currentTimeMillis() - lastFedTime;
        long requiredTime = type.getProductionTime() * 1000L;

        if (elapsed >= requiredTime) {
            isReadyToProduce.set(true);
        }
    }

    public void resetProduction() {
        this.isReadyToProduce.set(false);
        this.isHungry.set(true);
    }

    public String getProducedProductName() {
        return type.getProducedProductName();
    }

    public String getName() {
        return type.getName();
    }

    public AnimalType getType() {
        return type;
    }

    public BooleanProperty isHungryProperty() {
        return isHungry;
    }

    public BooleanProperty isReadyToProduceProperty() {
        return isReadyToProduce;
    }
}