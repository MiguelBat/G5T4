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
        JButton createAccountButton = new JButton("Create Account"); // Add create account button

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

        // Add action listener to the create account button
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch to the create account page
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(LoginPage.this);
                frame.getContentPane().removeAll();
                frame.getContentPane().add(new CreateAccountPage());
                frame.revalidate();
                frame.repaint();
            }
        });

        // Add components to the panel
        setLayout(new GridLayout(4, 2)); // Increase grid rows to accommodate the new button
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JPanel()); // Empty panel for spacing
        add(loginButton);
        add(createAccountButton); // Add the create account button
    }

    // Method to move to the main program with the user ID
    private void moveToMainProgram(int userId) {
        JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        DemoApplication demoApp = new DemoApplication(userId, loginFrame);
        demoApp.startMainProgram();
    }
}
