package com.example.common.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        String[] databases = {
                "accounting_db",
                "order_db",
                "inventory_db",
                "auth_db",
                "banking_user_db",
                "mp02_db",
                "notification_db"
        };

        for (String dbName : databases) {
            try {
                jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
                System.out.println("✅ Database '" + dbName + "' created or already exists");
            } catch (Exception e) {
                System.out.println("⚠️ Database '" + dbName + "' creation failed: " + e.getMessage());
            }
        }
    }
}
