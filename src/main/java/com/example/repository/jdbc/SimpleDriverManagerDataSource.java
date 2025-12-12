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
     * Create a DataSource configured with the specified JDBC URL and default credentials.
     *
     * @param url      the JDBC connection URL
     * @param username the default username to use when creating connections
     * @param password the default password to use when creating connections
     */
    public SimpleDriverManagerDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Obtain a JDBC Connection using the configured URL and the data source's default username and password.
     *
     * @return a new {@link Connection} to the configured JDBC URL authenticated with the data source's username and password
     * @throws SQLException if a database access error occurs or the connection cannot be established
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Obtain a JDBC Connection to the configured URL using the provided credentials.
     *
     * @param username the username to use for the new connection (overrides the datasource's default)
     * @param password the password to use for the new connection (overrides the datasource's default)
     * @return a new Connection to the datasource URL
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
 * Signals that this DataSource cannot be unwrapped to the requested interface.
 *
 * @param <T> the interface type to unwrap to
 * @param iface the interface class to check for unwrapping support
 * @throws UnsupportedOperationException always thrown because unwrapping is not supported
 */

    @Override public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
    /**
 * Indicates whether this DataSource implements or wraps the specified interface.
 *
 * @param iface the interface to check
 * @return `false` always; this DataSource does not wrap or expose any underlying implementation
 */
@Override public boolean isWrapperFor(Class<?> iface) { return false; }
    /**
 * Provide the PrintWriter used for logging by this DataSource.
 *
 * @return the `PrintWriter` used for logging, or `null` if none is configured
 */
@Override public java.io.PrintWriter getLogWriter() { return null; }
    /**
 * Accepts a PrintWriter for DataSource logging but performs no action.
 *
 * @param out the PrintWriter to set; this implementation ignores the value
 */
@Override public void setLogWriter(java.io.PrintWriter out) {}
    /**
 * No-op implementation; setting the login timeout has no effect for this DataSource.
 *
 * @param seconds the login timeout in seconds (ignored)
 */
@Override public void setLoginTimeout(int seconds) {}
    /**
 * Get the login timeout in seconds; this implementation does not configure a timeout.
 *
 * @return the login timeout in seconds, always 0
 */
@Override public int getLoginTimeout() { return 0; }
    /**
 * Provides the parent logger used by this DataSource.
 *
 * @return the global {@link java.util.logging.Logger} instance used as the parent logger
 */
@Override public java.util.logging.Logger getParentLogger() { return java.util.logging.Logger.getGlobal(); }
}