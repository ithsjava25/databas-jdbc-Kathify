package com.example.repository.jdbc;

import com.example.Account;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-implementation av AccountRepository med PBKDF2-lösenordshantering.
 */
public class JdbcAccountRepository implements AccountRepository {

    private final DataSource ds;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    /**
     * Creates a JdbcAccountRepository that uses the provided DataSource for database access.
     *
     * @param ds the DataSource used to obtain JDBC connections
     */
    public JdbcAccountRepository(DataSource ds) {
        this.ds = ds;
    }

    /**
     * Retrieve an account by its account name.
     *
     * @param name the account name to look up
     * @return an {@link Optional} containing the matching {@link Account} if found, otherwise {@link Optional#empty()}
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
     * Creates a new account and persists it to the database with a securely hashed password.
     *
     * Generates a cryptographic salt, hashes the account's plaintext password using PBKDF2, encodes
     * the salt in Base64, and inserts the account's name, hashed password, salt, first name, last name,
     * and SSN into the account table.
     *
     * @param account the account to create; the account's plaintext password will be hashed and the
     *                resulting hash and generated salt will be stored in the database
     */
    @Override
    public void create(Account account) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO account(name, password, salt, first_name, last_name, ssn) VALUES (?,?,?,?,?,?)")) {

            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(account.password(), salt);

            ps.setString(1, account.name());
            ps.setString(2, hashedPassword);
            ps.setString(3, Base64.getEncoder().encodeToString(salt));
            ps.setString(4, account.firstName());
            ps.setString(5, account.lastName());
            ps.setString(6, account.ssn());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the account's password for the given user ID and stores it hashed with a new random salt.
     *
     * @param userId the identifier of the account to update
     * @param newPassword the plaintext new password to be hashed and stored
     */
    @Override
    public void updatePassword(long userId, String newPassword) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE account SET password = ?, salt = ? WHERE user_id = ?")) {

            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(newPassword, salt);

            ps.setString(1, hashedPassword);
            ps.setString(2, Base64.getEncoder().encodeToString(salt));
            ps.setLong(3, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the account with the given user ID.
     *
     * @param userId the account's user_id primary key
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
     * @return a list of Account objects; empty if no accounts are found or if a database error occurs
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
     * Create an Account instance from the current row of the given ResultSet.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return the Account populated from the current ResultSet row
     * @throws SQLException if reading any column from the ResultSet fails
     */
    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("password"), // hashat lösenord
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("ssn")
        );
    }

    /**
     * Generates a cryptographically secure random salt.
     *
     * @return a 16-byte cryptographically secure random salt
     */

    private byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a password using PBKDF2 with HMAC-SHA256 and the provided salt.
     *
     * @param password the plaintext password to hash
     * @param salt the cryptographic salt as a byte array
     * @return the resulting hash encoded as a Base64 string
     * @throws RuntimeException if the hashing operation fails
     */
    private String hashPassword(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Fel vid hashning av lösenord", e);
        }
    }

    /**
     * Checks whether a plaintext password matches a stored password hash and salt.
     *
     * @param password    the plaintext password to verify
     * @param storedHash  the Base64-encoded stored password hash
     * @param storedSalt  the Base64-encoded salt used to produce the stored hash
     * @return            `true` if the password, when hashed with the decoded salt, equals the stored hash, `false` otherwise
     */
    public boolean verifyPassword(String password, String storedHash, String storedSalt) {
        byte[] salt = Base64.getDecoder().decode(storedSalt);
        String hashOfInput = hashPassword(password, salt);
        return hashOfInput.equals(storedHash);
    }
}