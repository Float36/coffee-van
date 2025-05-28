package com.yurii.coffeevan.coffeevan.db;

import com.yurii.coffeevan.coffeevan.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VanService {
    
    public VanService() {
        // Перевіряємо, чи встановлені тестові налаштування
        // isTestMode = System.getProperty("db.url") != null && 
        //              System.getProperty("db.url").contains("h2:mem:testdb");
    }
    
    public static class VanEntry {
        private final int id;
        private final int totalVolume;
        private final Timestamp createdAt;
        
        public VanEntry(int id, int totalVolume, Timestamp createdAt) {
            this.id = id;
            this.totalVolume = totalVolume;
            this.createdAt = createdAt;
        }
        
        public int getId() { return id; }
        public int getTotalVolume() { return totalVolume; }
        public Timestamp getCreatedAt() { return createdAt; }
    }

    public int saveVan(Van van) {
        int vanId = -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert van
                try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO vans (total_volume) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
                )) {
                    stmt.setInt(1, van.getTotalVolume());
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            vanId = rs.getInt(1);
                        }
                    }
                }

                // Insert coffee
                try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO coffee (van_id, name, type, volume, price, weight, quality, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                )) {
                    for (Coffee coffee : van.getAllCoffee()) {
                        stmt.setInt(1, vanId);
                        stmt.setString(2, coffee.getName());
                        stmt.setString(3, coffee.getType());
                        stmt.setInt(4, coffee.getVolume());
                        stmt.setDouble(5, coffee.getPrice());
                        stmt.setInt(6, coffee.getWeight());
                        stmt.setInt(7, coffee.getQuality());
                        stmt.setInt(8, coffee.getQuantity());
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return vanId;
    }

    public Van loadVan(int vanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First check if van exists
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vans WHERE id = ?")) {
                stmt.setInt(1, vanId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return null;
                }
            }

            // Load coffee for the van
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM coffee WHERE van_id = ?")) {
                stmt.setInt(1, vanId);
                ResultSet rs = stmt.executeQuery();

                Van van = new Van();
                while (rs.next()) {
                    Coffee coffee = createCoffeeFromResultSet(rs);
                    van.addCoffee(coffee);
                }
                return van;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<VanEntry> loadAllVans() {
        List<VanEntry> vans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM vans ORDER BY created_at DESC");
            while (rs.next()) {
                vans.add(new VanEntry(
                    rs.getInt("id"),
                    rs.getInt("total_volume"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vans;
    }

    public boolean deleteVan(int vanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete coffee first due to foreign key constraint
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM coffee WHERE van_id = ?")) {
                    stmt.setInt(1, vanId);
                    stmt.executeUpdate();
                }

                // Then delete van
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM vans WHERE id = ?")) {
                    stmt.setInt(1, vanId);
                    int rowsAffected = stmt.executeUpdate();
                    conn.commit();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Coffee createCoffeeFromResultSet(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String type = rs.getString("type");
        int volume = rs.getInt("volume");
        double price = rs.getDouble("price");
        int weight = rs.getInt("weight");
        int quality = rs.getInt("quality");
        int quantity = rs.getInt("quantity");

        return switch (type) {
            case "Зернова" -> new BeanCoffee(name, volume, price, weight, quality, quantity);
            case "Мелена" -> new GroundCoffee(name, volume, price, weight, quality, quantity);
            case "Розчинна (банка)" -> new InstantJarCoffee(name, volume, price, weight, quality, quantity);
            case "Розчинна (пакетик)" -> new InstantPacketCoffee(name, volume, price, weight, quality, quantity);
            default -> new Coffee(name, type, volume, price, weight, quality, quantity);
        };
    }
} 