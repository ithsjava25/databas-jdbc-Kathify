package com.example;

public record Account(
        long userId,
        String name,
        String password,
        String firstName,
        String lastName,
        String ssn
) {}
