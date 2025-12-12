package com.example;

/**
 * representerar ett användarkonto
 * <p>
 * innehåller information om användarens ID, namn, lösenord (hashat),
 * förnamn, efternamn o personnummer
 *
 * @param userId  ID för användaren
 * @param name   användarnamn
 * @param password  hashat lösenord
 * @param firstName förnamn
 * @param lastName efternamn
 * @param ssn  personnummer
 */
public record Account(
        long userId,
        String name,
        String password,
        String firstName,
        String lastName,
        String ssn
) {}