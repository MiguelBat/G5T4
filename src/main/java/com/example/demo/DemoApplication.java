package com.example.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@SpringBootApplication
public class DemoApplication implements DarkModeListener {

    private boolean sidebarOpen = false;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private boolean currentDarkModeState = false; // Initially set to false

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            // Set headless mode
            System.setProperty("java.awt.headless", "false");

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
            button4.addActionListener(e -> switchContent(frame, "Content4.java"));
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
        };
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

    private void openSettingsDialog(JFrame parent) {
        // Clear existing content
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();

        // Create a new instance of SettingsDialog and pass the current dark mode state
        SettingsDialog settingsDialog = new SettingsDialog(currentDarkModeState, this);
        contentPanel.add(settingsDialog, BorderLayout.CENTER);
    }

    @Override
    public void darkModeToggled(boolean darkModeEnabled) {
        currentDarkModeState = darkModeEnabled;
        contentPanel.setBackground(darkModeEnabled ? Color.DARK_GRAY : Color.WHITE);
    }
}
