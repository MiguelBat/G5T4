package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class EditDayDialog extends JDialog {

    

    public EditDayDialog(LocalDate date) {
        setTitle("Edit Day");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this dialog
        setLocationRelativeTo(null); // Center the dialog on the screen

        // Create and add components to the dialog
        JPanel panel = new JPanel();
        JLabel dateLabel = new JLabel("Date: " + date.toString());
        panel.add(dateLabel);

        add(panel);
    }
}