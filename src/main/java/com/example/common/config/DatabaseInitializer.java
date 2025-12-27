package com.example.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
        // Initialize databases immediately when bean is created
        initializeDatabase();
    }

    private void initializeDatabase() {
        String[] databases = {
                "accounting_db",
                "order_db",
                "inventory_db",
                "auth_db",
                "banking_user_db",
                "mp02_db",
                "notification_db"
        };

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String dbName : databases) {
                try {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
                    System.out.println("✅ Database '" + dbName + "' created or already exists");
                } catch (Exception e) {
                    System.out.println("⚠️ Database '" + dbName + "' creation failed: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize databases: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
