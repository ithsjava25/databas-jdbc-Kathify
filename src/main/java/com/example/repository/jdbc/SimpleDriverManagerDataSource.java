package com.example.repository.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleDriverManagerDataSource implements DataSource {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Creates a data source that obtains Connections from DriverManager using the provided JDBC URL and default credentials.
     *
     * @param url the JDBC URL used to create connections
     * @param username the default username used when creating connections
     * @param password the default password used when creating connections
     */
    public SimpleDriverManagerDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Obtain a new JDBC connection using the data source's configured URL and default credentials.
     *
     * @return a {@link java.sql.Connection} connected to the configured JDBC URL
     * @throws java.sql.SQLException if a database access error occurs or the URL is invalid
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Obtain a Connection to the configured JDBC URL using the provided credentials.
     *
     * @param username the database username to use for this connection
     * @param password the database password to use for this connection
     * @return a new {@link Connection} authenticated with the supplied username and password
     * @throws SQLException if a database access error occurs or the URL is invalid
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
 * Attempt to return this object as an instance of the specified interface.
 *
 * @param <T>   the target interface type
 * @param iface the interface class to unwrap to
 * @return an instance implementing the specified interface
 * @throws UnsupportedOperationException always thrown by this DataSource implementation
 */
@Override public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
    /**
 * Determines whether this DataSource can be unwrapped to the given interface.
 *
 * <p>This implementation does not support wrapping and always returns false.</p>
 *
 * @param iface the interface to check
 * @return true if this DataSource is a wrapper for the given interface, false otherwise
 */
@Override public boolean isWrapperFor(Class<?> iface) { return false; }
    /**
 * Get the log writer used by this DataSource.
 *
 * @return the configured {@link java.io.PrintWriter}, or `null` if no log writer is set
 */
@Override public java.io.PrintWriter getLogWriter() { return null; }
    /**
 * Sets the PrintWriter used for logging; this implementation ignores the provided writer.
 *
 * @param out the PrintWriter to use for logging (ignored)
 */
@Override public void setLogWriter(java.io.PrintWriter out) {}
    /**
 * Accepts a login timeout value but has no effect in this implementation.
 *
 * @param seconds the requested login timeout in seconds; ignored by this data source
 */
@Override public void setLoginTimeout(int seconds) {}
    /**
 * Get the maximum time, in seconds, this data source will wait when attempting to connect.
 *
 * @return the login timeout in seconds; `0` when no timeout is configured
 */
@Override public int getLoginTimeout() { return 0; }
    /**
 * Get the parent Logger associated with this data source.
 *
 * @return the global {@link java.util.logging.Logger} instance used as the parent logger
 */
@Override public java.util.logging.Logger getParentLogger() { return java.util.logging.Logger.getGlobal(); }
}