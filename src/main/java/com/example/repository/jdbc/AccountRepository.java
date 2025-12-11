package com.example.repository.jdbc;

import com.example.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByName(String name);
    void create(Account account);
    void updatePassword(long userId, String newPassword);
    void delete(long userId);
    List<Account> findAll();
}
