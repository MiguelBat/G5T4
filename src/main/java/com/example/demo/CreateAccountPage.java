package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateAccountPage extends JPanel {

    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton createAccountButton;

    public CreateAccountPage() {
        // Initialize UI components
        emailField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        createAccountButton = new JButton("Create Account");

        // Add action listener to the create account button
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get email, username, and password from input fields
                String email = emailField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Create account
                CreateAccountDB createAccountDB = new CreateAccountDB();
                boolean success = createAccountDB.createAccount(email, username, password);
                
                // Handle account creation result
                if (success) {
                    // Show success message
                    JOptionPane.showMessageDialog(null, "Account created successfully.");
                } else {
                    // Show error message
                    JOptionPane.showMessageDialog(null, "Failed to create account. Please try again.");
                }
            }
        });

        // Add components to the panel
        setLayout(new GridLayout(4, 2)); // Increase grid rows to accommodate the new button
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JPanel()); // Empty panel for spacing
        add(createAccountButton); // Add the create account button
    }
}

