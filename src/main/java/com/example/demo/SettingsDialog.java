package com.example.demo;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JPanel {

    private boolean darkModeEnabled = false;
    private DarkModeListener darkModeListener;

    public SettingsDialog(boolean initialDarkModeState, DarkModeListener darkModeListener) {
        this.darkModeListener = darkModeListener; // Store the listener
        this.darkModeEnabled = initialDarkModeState; // Store the initial dark mode state

        // Create and configure the SettingsDialog UI components...
        JToggleButton switchButton = new JToggleButton(); // Assuming switchButton is a JToggleButton
        setBackground(initialDarkModeState ? Color.DARK_GRAY : Color.WHITE);
        switchButton.setText(initialDarkModeState ? "Light Mode" : "Dark Mode");

        // Add action listener to the switch button
        switchButton.addActionListener(e -> toggleDarkMode(switchButton));
        
        // Add the switch button to the panel
        add(switchButton);
    }

    private void toggleDarkMode(JToggleButton switchButton) {
        darkModeEnabled = !darkModeEnabled;
        switchButton.setText(darkModeEnabled ? "Modo Claro" : "Modo Escuro");
        setBackground(darkModeEnabled ? Color.DARK_GRAY : Color.WHITE);

        // Notify the listener
        if (darkModeListener != null) {
            darkModeListener.darkModeToggled(darkModeEnabled);
        }
    }
}
