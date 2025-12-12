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
     * Skapar ett JdbcAccountRepository med angiven DataSource.
     */
    public JdbcAccountRepository(DataSource ds) {
        this.ds = ds;
    }

    /**
     * Hämtar ett konto från databasen baserat på användarnamnet.
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
     * skapar ett nytt konto i databasen
     *
     * @param account Kontot som ska skapas
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
     * uppdaterar lösenordet för ett konto med angivet userId
     *
     * @param userId ID:t för användaren vars lösenord ska uppdateras
     * @param newPassword det nya lösenordet för användaren
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
     * tar bort ett konto från databasen baserat på userId
     *
     * ID:t för användaren vars konto ska tas bort
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
     * hämtar alla konton från databasen
     *
     * return En lista med alla konton
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
     * mappar en rad från ResultSet till ett Account-objekt
     *
     * @param rs ResultSet som innehåller datan för en rad
     * @return Ett Account-objekt
     * @throws SQLException Om det uppstår ett fel vid hämtning av data
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