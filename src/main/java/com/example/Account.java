package com.example;

/**
 * representerar ett användarkonto
 */
public record Account(
        long userId,
        String name,
        String password,
        String firstName,
        String lastName,
        String ssn
) {}
