package com.example.repository.jdbc;

import com.example.Account;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAccountRepository implements AccountRepository {
    private final DataSource ds;

    /**
     * Creates a JdbcAccountRepository backed by the given DataSource.
     */
    public JdbcAccountRepository(DataSource ds) {
        this.ds = ds;
    }

    /**
     * Look up an account by its name.
     *
     * @param name the account name to search for
     * @return an Optional containing the matching Account if one exists, Optional.empty() otherwise
     */
    @Override
    public Optional<Account> findByName(String name) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM account WHERE name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Insert a new account record into the database.
     *
     * @param account the account to insert; its name, password, firstName, lastName, and ssn fields
     *                are persisted to the corresponding columns
     */
    @Override
    public void create(Account account) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO account(name, password, first_name, last_name, ssn) VALUES (?,?,?,?,?)")) {
            ps.setString(1, account.name());
            ps.setString(2, account.password());
            ps.setString(3, account.firstName());
            ps.setString(4, account.lastName());
            ps.setString(5, account.ssn());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the account password for the specified user ID.
     *
     * @param userId      the account's user_id primary key
     * @param newPassword the new password value to store for the account
     */
    @Override
    public void updatePassword(long userId, String newPassword) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE account SET password = ? WHERE user_id = ?")) {
            ps.setString(1, newPassword);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the account with the specified user ID from the repository.
     *
     * @param userId the identifier of the account to delete
     */
    @Override
    public void delete(long userId) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM account WHERE user_id = ?")) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve all accounts from the database.
     *
     * @return a list of Account objects representing every row in the `account` table; returns an empty list if no rows are found or if a database error occurs.
     */
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM account");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    /**
     * Map the current row of the given ResultSet to an Account.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return an Account populated from the current ResultSet row
     * @throws SQLException if a database access error occurs or required columns are missing
     */
    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("ssn")
        );
    }
}