package com.example.repository.jdbc;

import com.example.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    /**
 * Look up an account by its name.
 *
 * @param name the account name to search for
 * @return an Optional containing the Account with the given name, or Optional.empty() if none exists
 */
Optional<Account> findByName(String name);
    /**
 * Persist a new Account in the repository.
 *
 * @param account the Account to create in the repository
 */
void create(Account account);
    /**
 * Update the password of the account with the given userId.
 *
 * @param userId the unique identifier of the account whose password will be updated
 * @param newPassword the new password to set for the account
 */
void updatePassword(long userId, String newPassword);
    /**
 * Deletes the Account identified by the given userId from the repository.
 *
 * @param userId the identifier of the Account to delete
 */
void delete(long userId);
    /**
 * Retrieves all Account entities in the repository.
 *
 * @return a list containing every Account in the repository; empty if none exist.
 */
List<Account> findAll();
}