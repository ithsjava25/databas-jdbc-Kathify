package com.example.repository.jdbc;

import com.example.Account;
import java.util.List;
import java.util.Optional;

/**
 * interface för hantering av konton i databasen
 */
public interface AccountRepository {

    /**
     * hämtar ett konto baserat på användarnamn
     *
     * @param name användarnamn
     * @return ett Optional med kontot om det finns
     */
    Optional<Account> findByName(String name);

    /**
     * skapar ett nytt konto
     *
     * @param account kontot som ska skapas
     */
    void create(Account account);

    /**
     * uppdaterar lösenordet för ett konto
     *
     * @param userId ID för användaren
     * @param newPassword det nya lösenordet
     */
    void updatePassword(long userId, String newPassword);

    /**
     * tar bort ett konto
     *
     * @param userId ID för användaren som ska tas bort
     */
    void delete(long userId);

    /**
     * hämtar alla konton.
     *
     * @return lista med alla konton
     */
    List<Account> findAll();
}