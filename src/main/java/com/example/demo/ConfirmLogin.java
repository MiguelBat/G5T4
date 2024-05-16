package com.example.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfirmLogin {
    // Method to verify credentials and retrieve user ID
    public int verifyCredentials(String username, String password) {
        int userId = -1; // Initialize with a default value
        
        // SQL query to check if the username and password match
        String query = "SELECT ID FROM utilizadores WHERE nome = ? AND Password = SHA1(?)";
        
        // Establish a database connection
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Set the parameters for the prepared statement
            statement.setString(1, username);
            statement.setString(2, password);
            
            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                // If there is a result, the credentials are valid
                if (resultSet.next()) {
                    userId = resultSet.getInt("ID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying credentials: " + e.getMessage());
        }
        
        return userId;
    }
}