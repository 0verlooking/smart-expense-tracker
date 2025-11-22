package com.expensetracker.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for test data
 * Run this class to generate hashes for passwords
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Generating BCrypt hashes for test passwords...\n");

        // Admin password: admin123
        String adminPassword = "admin123";
        String adminHash = encoder.encode(adminPassword);
        System.out.println("Admin (username: admin, password: " + adminPassword + "):");
        System.out.println(adminHash);
        System.out.println();

        // User password: user123
        String userPassword = "user123";
        String userHash = encoder.encode(userPassword);
        System.out.println("User (username: user, password: " + userPassword + "):");
        System.out.println(userHash);
        System.out.println();

        System.out.println("Copy these hashes to data.sql file");
    }
}
