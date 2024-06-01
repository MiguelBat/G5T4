package com.example.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CreateAccountDB {
    public boolean createAccount(String email, String username, String password) {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the current date and time
        String datacreation = now.format(formatter);

        // Implement database logic to insert account information
        // Return true if account creation is successful, false otherwise
        String query = "INSERT INTO utilizadores (Nome, email, Password, Datacriada) VALUES (?, ?, SHA1(?), ?)";

        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, datacreation); // Set the current date and time

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        }
    }
}
