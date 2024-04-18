package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        // Initialize UI components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

        // Add action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get username and password from input fields
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Verify credentials
                ConfirmLogin confirmLogin = new ConfirmLogin();
                int userId = confirmLogin.verifyCredentials(username, password);
                
                // Handle login result
                if (userId != -1) {
                    // Move to main program with user ID
                    moveToMainProgram(userId);
                } else {
                    // Show error message
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                }
            }
        });

        // Add components to the panel
        setLayout(new GridLayout(3, 2));
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JPanel()); // Empty panel for spacing
        add(loginButton);
    }

    // Method to move to the main program with the user ID
    private void moveToMainProgram(int userId) {
        JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        DemoApplication demoApp = new DemoApplication(userId, loginFrame);
        demoApp.startMainProgram();
    }
}
