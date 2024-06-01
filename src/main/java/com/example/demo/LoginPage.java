package com.example.demo;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginPage extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckBox;

    public LoginPage() {
        // Initialize UI components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        rememberMeCheckBox = new JCheckBox("Remember Me");
        JButton loginButton = new JButton("Entrar");
        JButton createAccountButton = new JButton("Criar conta"); // Add create account button

        // Preload saved credentials if available
        loadSavedCredentials();

        // Add action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get username and password from input fields
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean rememberMe = rememberMeCheckBox.isSelected();

                // Verify credentials
                ConfirmLogin confirmLogin = new ConfirmLogin();
                int userId = confirmLogin.verifyCredentials(username, password);

                // Handle login result
                if (userId != -1) {
                    // If Remember Me is selected, store credentials
                    if (rememberMe) {
                        try {
                            confirmLogin.storeCredentials(username, password);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Failed to store credentials.");
                        }
                    }
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
        setLayout(new GridLayout(5, 2)); // Increase grid rows to accommodate the new checkbox
        add(new JLabel("Utilizador:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(rememberMeCheckBox); // Add the Remember Me checkbox
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

    // Method to load saved credentials if available
    private void loadSavedCredentials() {
        ConfirmLogin confirmLogin = new ConfirmLogin();
        try {
            String[] credentials = confirmLogin.loadCredentials();
            if (credentials != null) {
                usernameField.setText(credentials[0]);
                passwordField.setText(credentials[1]);
                rememberMeCheckBox.setSelected(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load saved credentials.");
        }
    }
}
