package main.java.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

public class Enclosure {
    private final IntegerProperty capacity = new SimpleIntegerProperty(5);
    private final double x, y;

    public Enclosure(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}