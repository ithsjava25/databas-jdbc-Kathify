package com.example.repository.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * en enkel DataSource som använder DriverManager för att skapa anslutningar
 */
public class SimpleDriverManagerDataSource implements DataSource {

    private final String url;
    private final String username;
    private final String password;

    /**
     * skapar en DataSource med given URL och inloggning
     */
    public SimpleDriverManagerDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * hämtar en anslutning med standardanvändaren
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * hämtar en anslutning med angiven användare och lösenord
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // standardmetoder som inte används i denna enkla implementation

    @Override public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
    @Override public boolean isWrapperFor(Class<?> iface) { return false; }
    @Override public java.io.PrintWriter getLogWriter() { return null; }
    @Override public void setLogWriter(java.io.PrintWriter out) {}
    @Override public void setLoginTimeout(int seconds) {}
    @Override public int getLoginTimeout() { return 0; }
    @Override public java.util.logging.Logger getParentLogger() { return java.util.logging.Logger.getGlobal(); }
}