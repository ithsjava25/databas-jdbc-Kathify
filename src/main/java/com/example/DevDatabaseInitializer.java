package com.example;

import org.testcontainers.containers.MySQLContainer;

/**
 * initierar en utvecklingsdatabas med Testcontainers
 * skapar och startar en MySQL-container med förinställd databas,
 * användarnamn, lösenord o init-skript. Sätter även systemegenskaper
 * för JDBC URL, användare o lösenord
 */
public class DevDatabaseInitializer {

    private static MySQLContainer<?> mysql;

    /**
     * Initialize and start a Testcontainers MySQL container for development if it is not already running.
     *
     * When a new container is created and started, this method sets the system properties
     * "APP_JDBC_URL", "APP_DB_USER", and "APP_DB_PASS" to the container's JDBC URL, username, and password respectively.
     */
    public static void start() {
        if (mysql == null) {
            mysql = new MySQLContainer<>("mysql:9.5.0")
                    .withDatabaseName("testdb")
                    .withUsername("user")
                    .withPassword("password")
                    .withConfigurationOverride("myconfig")
                    .withInitScript("init.sql");
            mysql.start();

            System.setProperty("APP_JDBC_URL", mysql.getJdbcUrl());
            System.setProperty("APP_DB_USER", mysql.getUsername());
            System.setProperty("APP_DB_PASS", mysql.getPassword());
        }
    }
}