package com.example.repository.jdbc;

import com.example.Account;
import java.util.List;
import java.util.Optional;

/**
 * interface för hantering av konton i databasen
 */
public interface AccountRepository {

    /**
 * Finds an account by username.
 *
 * @param name the username to look up
 * @return an Optional containing the Account when a matching username exists, otherwise an empty Optional
 */
    Optional<Account> findByName(String name);

    /**
 * Creates a new account in the repository.
 *
 * @param account the Account to create
 */
    void create(Account account);

    /**
 * Update the password for the account with the given user ID.
 *
 * @param userId the ID of the account to update
 * @param newPassword the new password to set for the account
 */
    void updatePassword(long userId, String newPassword);

    /**
 * Delete an account by its user ID.
 *
 * @param userId the ID of the account to delete
 */
    void delete(long userId);

    /**
 * Retrieve all accounts.
 *
 * @return a list of Account objects containing all accounts
 */
    List<Account> findAll();
}