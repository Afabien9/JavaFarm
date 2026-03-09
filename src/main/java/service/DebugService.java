package main.java.service;

public class DebugService {
    private static DebugService instance;
    public static DebugService getInstance() {
        if (instance == null) instance = new DebugService();
        return instance;
    }

    public void addCheatMoney(int amount) {
        GameService.getInstance().getWallet().addMoney(amount);
        System.out.println("Debug: + " + amount + "€ ajoutés.");
    }
}