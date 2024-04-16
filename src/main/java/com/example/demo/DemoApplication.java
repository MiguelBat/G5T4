package com.example.demo;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

@SpringBootApplication
public class DemoApplication {

    private boolean darkModeEnabled = false;

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

            // Create menu bar
            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("Options");
            JMenuItem darkModeMenuItem = new JMenuItem(getModeText());
            darkModeMenuItem.addActionListener(e -> toggleDarkMode(frame, darkModeMenuItem));
            menu.add(darkModeMenuItem);
            menuBar.add(menu);
            frame.setJMenuBar(menuBar);

            // Display the window
            frame.setVisible(true);
        };
    }

    private String getModeText() {
        return darkModeEnabled ? "Light Mode" : "Dark Mode";
    }

    private void toggleDarkMode(JFrame frame, JMenuItem menuItem) {
        darkModeEnabled = !darkModeEnabled;
        if (darkModeEnabled) {
            // Set dark mode
            frame.getContentPane().setBackground(Color.BLACK);
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    try {
                        // Set Nimbus as the current look and feel
                        UIManager.setLookAndFeel(info.getClassName());
                        // Update UI to reflect changes
                        updateComponentTreeUI(frame);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } else {
            // Set light mode
            frame.getContentPane().setBackground(Color.WHITE);
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    try {
                        // Set Metal as the current look and feel
                        UIManager.setLookAndFeel(info.getClassName());
                        // Update UI to reflect changes
                        updateComponentTreeUI(frame);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        // Update menu item text
        menuItem.setText(getModeText());
    }

    private void updateComponentTreeUI(JFrame frame) {
        SwingUtilities.updateComponentTreeUI(frame);
        updateTextColors(frame.getContentPane());
    }

    private void updateTextColors(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel) {
                component.setForeground(darkModeEnabled ? Color.WHITE : Color.BLACK);
            } else if (component instanceof Container) {
                updateTextColors((Container) component);
            }
        }
    }
}