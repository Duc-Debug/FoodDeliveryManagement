package com.delivery.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class StoreConfig {
    private static Path filePath;

    private static String adminUsername = "admin";
    private static String adminPassword = "admin123";
    private static double storeBalance = 0.0;

    public StoreConfig() {
    }

    public static void init(Path path) {
        filePath = path;
        if (!Files.exists(filePath)) {
            save();
            return;
        }
        try {
            List<String> lines = Files.readAllLines(filePath);
            if (!lines.isEmpty()) {
                String[] parts = lines.get(0).split(",");
                adminUsername = parts[0];
                adminPassword = parts[1];
                storeBalance = Double.parseDouble(parts[2]);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Failed to load data from store.csv");
        }
    }

    public static void save() {
        if (filePath == null)
            return;
        try {
            String line = String.join(",", adminUsername, adminPassword, String.valueOf(storeBalance));
            Files.writeString(filePath, line);
        } catch (IOException e) {
            System.out.println("Failed to sync data to store.csv");
        }
    }

    public static boolean authenticate(String username, String password) {
        return adminUsername.equals(username) && adminPassword.equals(password);
    }

    public static void addRevenue(double amount) {
        if (amount > 0) {
            storeBalance += amount;
        }
    }

    public static double getStoreBalance() {
        return storeBalance;
    }

    public static String getAdminUsername() {
        return adminUsername;
    }
}
