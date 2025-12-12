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

    public JdbcAccountRepository(DataSource ds) {
        this.ds = ds;
    }

    /**
     * hämtar ett konto baserat på användarnamn.
     *
     * @param name användarnamnet att söka efter
     * @return Optional med Account om det finns, annars tomt
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
     * skapar ett nytt konto med hashat lösenord
     *
     * @param account kontot som ska sparas
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
     * uppdaterar lösenord o sparar det hashat med nytt salt
     *
     * @param userId ID för användaren vars lösenord ska ändras
     * @param newPassword det nya klartextlösenordet
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
     * tar bort ett konto baserat på userId
     *
     * @param userId id för kontot som ska tas bort
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
     * hämtar alla konton
     *
     * @return lista av Account
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
     * @param rs resultatsetets aktuella rad
     * @return ett Account baserat på data i resultatsetet
     * @throws SQLException om fält inte kan läsas
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
     * genererar ett nytt slumpmässigt salt för lösenordshashning
     *
     * @return en byte-array med slumpmässigt genererat salt (16 bytes)
     */
    private byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    /**
     * hashar ett lösenord med PBKDF2 o angivet salt
     *
     * @param password lösenordet som ska hashats
     * @param salt slumpmässigt salt som används vid hashning
     * @return Base64-sträng med det hashade lösenordet
     * @throws RuntimeException om hashning misslyckas
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
     * verifierar om ett angivet lösenord matchar ett lagrat hashat lösenord
     *
     * @param password klartextlösenordet som ska verifieras
     * @param storedHash hashvärdet som finns lagrat för användaren
     * @param storedSalt saltet (Base64-kodat) som användes vid hashningen
     * @return true om lösenordet matchar hash + salt, annars blir det false
     */
    public boolean verifyPassword(String password, String storedHash, String storedSalt) {
        byte[] salt = Base64.getDecoder().decode(storedSalt);
        String hashOfInput = hashPassword(password, salt);
        return hashOfInput.equals(storedHash);
    }
}
