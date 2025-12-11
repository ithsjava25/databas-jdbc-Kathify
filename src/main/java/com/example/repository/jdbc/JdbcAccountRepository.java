package com.example.repository.jdbc;

import com.example.Account;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAccountRepository implements AccountRepository {
    private final DataSource ds;

    public JdbcAccountRepository(DataSource ds) {
        this.ds = ds;
    }

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