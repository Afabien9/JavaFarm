package main.java.model;

import java.io.*;
import java.util.Properties;
import java.util.Set;
import main.java.controller.MapController;

public class SaveManager {

    private static final String SAVE_FILE = "game.save";

    public static void saveGame(Wallet wallet, Inventory inventory, MapController mapController) {
        Properties props = new Properties();

        if (wallet != null) {
            props.setProperty("money", String.valueOf(wallet.getMoney()));
        }

        if (inventory != null) {
            inventory.getSeeds().forEach((type, qty) ->
                    props.setProperty("seed_" + type.name(), String.valueOf(qty)));

            inventory.getProducts().forEach((type, qty) ->
                    props.setProperty("prod_" + type.name(), String.valueOf(qty)));

            inventory.getAnimalProducts().forEach((name, qty) ->
                    props.setProperty("animal_" + name.replace(" ", "_"), String.valueOf(qty)));
        }

        if (mapController != null) {
            Set<String> positions = mapController.getOccupiedPositions();
            int i = 0;
            for (String pos : positions) {
                String data = pos;
                if (pos.contains("enclosure")) {
                    String animalName = mapController.getAnimalAt(pos);
                    if (animalName != null) {
                        data += ":" + animalName;
                    }
                }
                props.setProperty("struct_" + i, data);
                i++;
            }
            props.setProperty("struct_count", String.valueOf(i));
        }

        try (OutputStream out = new FileOutputStream(SAVE_FILE)) {
            props.store(out, "JavaFarm Save Data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGame(Wallet wallet, Inventory inventory, MapController mapController) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);

            if (wallet != null) {
                wallet.moneyProperty().set(parseIntSafe(props.getProperty("money"), 500));
            }

            if (inventory != null) {
                for (CropType type : CropType.values()) {
                    int sQty = parseIntSafe(props.getProperty("seed_" + type.name()), 0);
                    inventory.getSeeds().put(type, sQty);

                    int pQty = parseIntSafe(props.getProperty("prod_" + type.name()), 0);
                    inventory.getProducts().put(type, pQty);
                }

                props.forEach((key, value) -> {
                    String k = (String) key;
                    if (k.startsWith("animal_")) {
                        String name = k.replace("animal_", "").replace("_", " ");
                        inventory.getAnimalProducts().put(name, Integer.parseInt((String) value));
                    }
                });
            }

            if (mapController != null) {
                int count = parseIntSafe(props.getProperty("struct_count"), 0);
                for (int i = 0; i < count; i++) {
                    String rawData = props.getProperty("struct_" + i);
                    if (rawData != null) {
                        String[] mainParts = rawData.split(":");
                        String posPart = mainParts[0];
                        String animalType = (mainParts.length > 1) ? mainParts[1] : null;

                        String[] parts = posPart.split("_");
                        double x = Double.parseDouble(parts[0]);
                        double y = Double.parseDouble(parts[1]);
                        String structType = parts[2];

                        if (structType.equals("plot")) {
                            mapController.loadPlot(x, y);
                        } else if (structType.equals("enclosure")) {
                            mapController.loadEnclosure(x, y, animalType);
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la sauvegarde.");
        }
    }

    public static void deleteSave() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private static int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}