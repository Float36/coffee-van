package com.yurii.coffeevan.coffeevan.db;

import com.yurii.coffeevan.coffeevan.model.*;
import com.yurii.coffeevan.coffeevan.model.Van;
import com.yurii.coffeevan.coffeevan.VansController.VanEntry;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class VanService {
    
    public static List<VanEntry> getAllVans() throws SQLException {
        String selectVansSQL = "SELECT id, total_volume, created_at FROM vans ORDER BY created_at DESC";
        List<VanEntry> vans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectVansSQL)) {
            
            while (rs.next()) {
                vans.add(new VanEntry(
                    rs.getInt("id"),
                    rs.getInt("total_volume"),
                    rs.getTimestamp("created_at")
                ));
            }
        }
        return vans;
    }
    
    public static int saveVan(Van van) throws SQLException {
        String insertVanSQL = "INSERT INTO vans (total_volume) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertVanSQL, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, van.getTotalVolume());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int vanId = generatedKeys.getInt(1);
                    saveCoffeeList(vanId, van.getAllCoffee());
                    return vanId;
                } else {
                    throw new SQLException("Failed to get van ID");
                }
            }
        }
    }

    public static void deleteVan(int vanId) throws SQLException {
        // Спочатку видаляємо всю каву з цього фургону
        String deleteCoffeeSQL = "DELETE FROM coffee WHERE van_id = ?";
        // Потім видаляємо сам фургон
        String deleteVanSQL = "DELETE FROM vans WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Встановлюємо автокоміт в false для транзакції
            conn.setAutoCommit(false);
            
            try {
                // Видаляємо каву
                try (PreparedStatement pstmt = conn.prepareStatement(deleteCoffeeSQL)) {
                    pstmt.setInt(1, vanId);
                    pstmt.executeUpdate();
                }
                
                // Видаляємо фургон
                try (PreparedStatement pstmt = conn.prepareStatement(deleteVanSQL)) {
                    pstmt.setInt(1, vanId);
                    pstmt.executeUpdate();
                }
                
                // Якщо все успішно, підтверджуємо транзакцію
                conn.commit();
            } catch (SQLException e) {
                // У разі помилки відкочуємо зміни
                conn.rollback();
                throw e;
            } finally {
                // Відновлюємо автокоміт
                conn.setAutoCommit(true);
            }
        }
    }
    
    private static void saveCoffeeList(int vanId, List<Coffee> coffeeList) throws SQLException {
        String insertCoffeeSQL = """
            INSERT INTO coffee (van_id, name, type, volume, price, weight, quality, quantity)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertCoffeeSQL)) {
            
            for (Coffee coffee : coffeeList) {
                pstmt.setInt(1, vanId);
                pstmt.setString(2, coffee.getName());
                pstmt.setString(3, coffee.getType());
                pstmt.setInt(4, coffee.getVolume());
                pstmt.setDouble(5, coffee.getPrice());
                pstmt.setInt(6, coffee.getWeight());
                pstmt.setInt(7, coffee.getQuality());
                pstmt.setInt(8, coffee.getQuantity());
                pstmt.executeUpdate();
            }
        }
    }

    public static List<Coffee> getVanCoffee(int vanId) throws SQLException {
        String selectCoffeeSQL = """
            SELECT name, type, volume, price, weight, quality, quantity 
            FROM coffee 
            WHERE van_id = ?
        """;
        
        List<Coffee> coffeeList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectCoffeeSQL)) {
            
            pstmt.setInt(1, vanId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    Coffee coffee = switch (type) {
                        case "Зернова" -> new BeanCoffee(
                            rs.getString("name"),
                            rs.getInt("volume"),
                            rs.getDouble("price"),
                            rs.getInt("weight"),
                            rs.getInt("quality"),
                            rs.getInt("quantity")
                        );
                        case "Мелена" -> new GroundCoffee(
                            rs.getString("name"),
                            rs.getInt("volume"),
                            rs.getDouble("price"),
                            rs.getInt("weight"),
                            rs.getInt("quality"),
                            rs.getInt("quantity")
                        );
                        case "Розчинна (банка)" -> new InstantJarCoffee(
                            rs.getString("name"),
                            rs.getInt("volume"),
                            rs.getDouble("price"),
                            rs.getInt("weight"),
                            rs.getInt("quality"),
                            rs.getInt("quantity")
                        );
                        case "Розчинна (пакетик)" -> new InstantPacketCoffee(
                            rs.getString("name"),
                            rs.getInt("volume"),
                            rs.getDouble("price"),
                            rs.getInt("weight"),
                            rs.getInt("quality"),
                            rs.getInt("quantity")
                        );
                        default -> throw new SQLException("Невідомий тип кави: " + type);
                    };
                    coffeeList.add(coffee);
                }
            }
        }
        return coffeeList;
    }
} 