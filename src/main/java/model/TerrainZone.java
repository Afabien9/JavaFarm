package main.java.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.ArrayList;
import java.util.List;

public class TerrainZone {
    private final List<Plot> plots = new ArrayList<>();
    private final BooleanProperty locked = new SimpleBooleanProperty(true);
    private final int price;

    public TerrainZone(int price, int plotCount) {
        this.price = price;
        for (int i = 0; i < plotCount; i++) {
            plots.add(new Plot(true));
        }
    }

    public void unlock() {
        locked.set(false);
        for (Plot p : plots) {
            p.setLocked(false);
        }
    }

    public List<Plot> getPlots() { return plots; }
    public boolean isLocked() { return locked.get(); }
    public int getPrice() { return price; }
}