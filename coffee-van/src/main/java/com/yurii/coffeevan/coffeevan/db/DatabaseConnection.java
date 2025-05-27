package com.yurii.coffeevan.coffeevan.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
            );
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create vans table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS vans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    total_volume INT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create coffee table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS coffee (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    van_id INT,
                    name VARCHAR(255) NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    volume INT NOT NULL,
                    price DOUBLE NOT NULL,
                    weight INT NOT NULL,
                    quality INT NOT NULL,
                    quantity INT NOT NULL,
                    FOREIGN KEY (van_id) REFERENCES vans(id)
                )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 