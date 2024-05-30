package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteDayDialog extends JDialog {

    private int userId;
    private int eventID;

    public DeleteDayDialog(int userId, int eventID) {
        this.userId = userId;
        this.eventID = eventID;

        setTitle("Delete Event");
        setSize(300, 150);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 1));

        JLabel confirmationLabel = new JLabel("Are you sure you want to delete this event?");
        confirmationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(confirmationLabel);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement deletion logic here
                deleteEvent(userId, eventID);
                dispose(); // Close the dialog after deletion
            }
        });
        panel.add(deleteButton);

        // Set the "Delete" button as the default button
        getRootPane().setDefaultButton(deleteButton);

        add(panel);
    }

    // Method to delete event
    private void deleteEvent(int userId, int eventID) {
        // Implement deletion logic here, e.g., execute DELETE query
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "DELETE FROM calendario WHERE IDutilizador = ? AND ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setInt(2, eventID);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle exception
        }
    }
}
