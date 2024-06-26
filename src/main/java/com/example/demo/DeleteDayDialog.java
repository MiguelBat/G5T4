package com.example.demo;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DeleteDayDialog extends JDialog {

    private int userId;
    private int eventID;

    public DeleteDayDialog(int userId, int eventID) {
        this.userId = userId;
        this.eventID = eventID;

        setTitle("Apagar Evento");
        setSize(300, 150);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(2, 1));

        JLabel confirmationLabel = new JLabel("Tem a certeza que quer apagar? esta ação é irreversivel");
        confirmationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(confirmationLabel);

        JButton deleteButton = new JButton("Apagar");
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
