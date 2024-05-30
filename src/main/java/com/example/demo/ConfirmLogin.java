package com.example.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ConfirmLogin {

    private static final String CREDENTIALS_FILE_PATH = System.getProperty("user.home") + "/credentials.txt";
    private static final String SECRET_KEY_FILE_PATH = System.getProperty("user.home") + "/secret.key";
    private static final String ALGORITHM = "AES";

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

    // Method to store credentials
    public void storeCredentials(String username, String password) throws IOException {
        SecretKey secretKey = getSecretKey();
        String credentials = "username=" + username + "\npassword=" + password;
        String encryptedCredentials = encrypt(credentials, secretKey);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE_PATH))) {
            writer.write(encryptedCredentials);
        }
    }

    // Method to load saved credentials
    public String[] loadCredentials() throws IOException {
        File file = new File(CREDENTIALS_FILE_PATH);
        if (!file.exists()) {
            return null;
        }

        SecretKey secretKey = getSecretKey();
        StringBuilder encryptedData = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                encryptedData.append(line);
            }
        }

        String decryptedCredentials = decrypt(encryptedData.toString(), secretKey);
        String[] credentialsArray = decryptedCredentials.split("\n");
        String username = credentialsArray[0].split("=")[1];
        String password = credentialsArray[1].split("=")[1];

        return new String[]{username, password};
    }

    // Method to generate and save the secret key
    private void saveSecretKey(SecretKey secretKey) throws IOException {
        byte[] encodedKey = secretKey.getEncoded();
        String encodedKeyString = Base64.getEncoder().encodeToString(encodedKey);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SECRET_KEY_FILE_PATH))) {
            writer.write(encodedKeyString);
        }
    }

    // Method to retrieve the secret key
    private SecretKey getSecretKey() throws IOException {
        File keyFile = new File(SECRET_KEY_FILE_PATH);
        if (!keyFile.exists()) {
            // Generate a new key if it doesn't exist
            SecretKey secretKey = generateSecretKey();
            saveSecretKey(secretKey);
            return secretKey;
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(keyFile))) {
                String encodedKeyString = reader.readLine();
                byte[] decodedKey = Base64.getDecoder().decode(encodedKeyString);
                return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
            }
        }
    }

    // Method to generate a new secret key
    private SecretKey generateSecretKey() throws IOException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256, new SecureRandom());
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new IOException("Error generating secret key", e);
        }
    }

    // Method to encrypt data
    private String encrypt(String data, SecretKey secretKey) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new IOException("Error encrypting data", e);
        }
    }

    // Method to decrypt data
    private String decrypt(String encryptedData, SecretKey secretKey) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new IOException("Error decrypting data", e);
        }
    }
}
