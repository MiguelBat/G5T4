package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;





@SpringBootApplication
public class DemoApplication implements DarkModeListener {

    private boolean sidebarOpen = false;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private boolean currentDarkModeState = false; // Initially set to false
    private static DemoApplication instance;
    
   
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired int userId;
    private JFrame loginFrame;

    public DemoApplication(int userId, JFrame loginFrame) {
        this.userId = userId;
        this.loginFrame = loginFrame;
    }
  
    
    public void startMainProgram() {
        // Implement the main program logic here
        // This method will be called when the user is logged in successfully
        System.out.println(userId);
        startMainApplication();
        // You can initialize your main program components, load data, etc.
    
        // Close the login window
        loginFrame.dispose();
    }

    public DemoApplication() {
        // Empty constructor
    }

    public DemoApplication(int userId) {
        this.userId = userId;
    }

    public static DemoApplication getInstance(int userId) {
        if (instance == null) {
            System.out.println("Creating new DemoApplication instance...");
            instance = new DemoApplication(userId);
        } else {
            System.out.println("Returning existing DemoApplication instance...");
            instance.setUserId(userId); // Ensure userId is updated if instance already exists
        }
        return instance;
    }
    

    // Setter method for userId
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("UserId set to: " + userId);
    }

    // Getter method for userId
    public int getUserId() {
        System.out.println("Returning userId: " + userId);
        return userId;
    }



    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            // Set headless mode
            System.setProperty("java.awt.headless", "false");

            // Check if the user is logged in
            if (isLoggedIn()) {
                // User is logged in, proceed with the main application
                startMainApplication();
            } else {
                // User is not logged in, open the login page
                openLoginPage();
            }
        };
    }

    private boolean isLoggedIn() {
        // Check if the user is logged in
        return userId != -1;
    }

    private void startMainApplication() {
        // Create the main window
        JFrame frame = new JFrame("G5T4");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel for the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
        JButton openSidebarButton = new JButton("☰"); // Hamburger icon
        openSidebarButton.setPreferredSize(new Dimension(50, 30)); // Set button size
        openSidebarButton.addActionListener(e -> toggleSidebar(frame));
        buttonPanel.add(openSidebarButton);

        // Add the panel with the button to the content pane at the top left corner
        frame.add(buttonPanel, BorderLayout.NORTH);

        // Create the sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(200, frame.getHeight()));

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the username
        sidebarPanel.add(usernameLabel, BorderLayout.NORTH);

        // Add buttons to the sidebar
        JButton button1 = new JButton("HomePage");
        JButton button2 = new JButton("Trabalhos");
        JButton button3 = new JButton("Chat");
        JButton button4 = new JButton("Calendario");
        JButton button5 = new JButton("Definições");

        // Add action listeners to the buttons
        button1.addActionListener(e -> switchContent(frame, "Content1.java"));
        button2.addActionListener(e -> switchContent(frame, "Content2.java"));
        button3.addActionListener(e -> switchContent(frame, "Content3.java"));
        button4.addActionListener(e -> openCalendar(frame));
        button5.addActionListener(e -> openSettingsDialog(frame));

        // Add buttons to the sidebar panel
        sidebarPanel.add(button1);
        sidebarPanel.add(button2);
        sidebarPanel.add(button3);
        sidebarPanel.add(button4);
        sidebarPanel.add(button5);

        // Add the sidebar panel to the frame, initially hidden
        frame.getContentPane().add(sidebarPanel, BorderLayout.WEST);
        sidebarPanel.setVisible(false);

        // Create the content panel
        contentPanel = new JPanel(new BorderLayout());
        frame.add(contentPanel, BorderLayout.CENTER);

        // Display the window
        frame.setVisible(true);
    }

    private void openLoginPage() {
        JFrame loginFrame = new JFrame("Login Page");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the LoginPage panel
        LoginPage loginPage = new LoginPage();

        // Add the LoginPage panel to the login frame
        loginFrame.getContentPane().add(loginPage);

        // Pack and display the login frame
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private void openSettingsDialog(JFrame parent) {
        // Clear existing content
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();

        // Create a new instance of SettingsDialog and pass the current dark mode state
        SettingsDialog settingsDialog = new SettingsDialog(currentDarkModeState, this);
        contentPanel.add(settingsDialog, BorderLayout.CENTER);

        // Apply dark/light mode colors to the settings dialog, top panel, and calendar
    }

    private void openCalendar(JFrame parent) {
        // Clear existing content
        contentPanel.removeAll();
        contentPanel.revalidate();

        // Create a new instance of CalendarPanel
        CalendarPanel calendarPanel = new CalendarPanel(userId);
        contentPanel.add(calendarPanel, BorderLayout.CENTER);

        // Apply dark/light mode colors to the calendar panel, top panel, and sidebar
    }

    private void toggleSidebar(JFrame frame) {
        sidebarOpen = !sidebarOpen;
        sidebarPanel.setVisible(sidebarOpen);
    }

    private void switchContent(JFrame frame, String contentFile) {
        // Load and display content from the specified file
        // Implement this method based on your project structure
        // For simplicity, we're not including the implementation here
    }

    @Override
    public void darkModeToggled(boolean darkModeEnabled) {
        currentDarkModeState = darkModeEnabled;
        if (darkModeEnabled) {
            applyDarkModeColors(sidebarPanel, contentPanel);
        } else {
            applyLightModeColors(sidebarPanel, contentPanel);
        }
    }

    // Method to apply dark mode colors to the components
    private void applyDarkModeColors(Component... components) {
        for (Component component : components) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                jComponent.setBackground(Color.DARK_GRAY);
                jComponent.setForeground(Color.WHITE);
            }
        }
        // Apply dark mode color to top panel
        if (sidebarPanel != null) {
            sidebarPanel.setBackground(Color.DARK_GRAY);
        }
        // Apply dark mode color to sidebar
        if (contentPanel != null) {
            contentPanel.setBackground(Color.DARK_GRAY);
        }
    }

    // Method to apply light mode colors to the components
    private void applyLightModeColors(Component... components) {
        for (Component component : components) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                jComponent.setBackground(Color.WHITE);
                jComponent.setForeground(Color.BLACK);
            }
        }
        // Apply light mode color to top panel
        if (sidebarPanel != null) {
            sidebarPanel.setBackground(Color.WHITE);
        }
        // Apply light mode color to sidebar
        if (contentPanel != null) {
            contentPanel.setBackground(Color.WHITE);
        }
    }
}
