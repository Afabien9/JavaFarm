package main.java.model;

import main.java.service.GameService;
import java.io.*;

public class SaveManager {
    private static final String FILE_NAME = "farm_data.txt";

    public static void saveGame() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            GameService gs = GameService.getInstance();

            writer.write(String.valueOf(gs.getWallet().getMoney()));
            writer.newLine();

            for (CropType type : CropType.values()) {
                int seedQty = gs.getInventory().getSeeds().getOrDefault(type, 0);
                int prodQty = gs.getInventory().getProducts().getOrDefault(type, 0);
                writer.write(type.name() + ":" + seedQty + ":" + prodQty);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGame() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            GameService gs = GameService.getInstance();

            int money = Integer.parseInt(reader.readLine());
            gs.getWallet().addMoney(money - gs.getWallet().getMoney());

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    CropType type = CropType.valueOf(parts[0]);
                    gs.getInventory().addSeed(type, Integer.parseInt(parts[1]));
                    gs.getInventory().addProduct(type, Integer.parseInt(parts[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}