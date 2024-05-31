package com.example.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DetailsDayDialog extends JDialog {

    private int eventID;
    private JLabel titleLabel;
    private JLabel startDateLabel;
    private JLabel endDateLabel;
    private JLabel detailsLabel;

    public DetailsDayDialog(int eventID, int userId) {
        this.eventID = eventID;

        setTitle("Event Details");
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        loadEventData();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);  // Adding padding
        gbc.anchor = GridBagConstraints.NORTHWEST;  // Align to top left

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        
        titleLabel = new JLabel();
        gbc.gridx = 1;
        panel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Start Date:"), gbc);
        
        startDateLabel = new JLabel();
        gbc.gridx = 1;
        panel.add(startDateLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("End Date:"), gbc);
        
        endDateLabel = new JLabel();
        gbc.gridx = 1;
        panel.add(endDateLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Details:"), gbc);
        
        detailsLabel = new JLabel();
        gbc.gridx = 1;
        panel.add(detailsLabel, gbc);

        add(panel);
    }

    private void loadEventData() {
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT Titulo, Data, datafim, detalhes FROM calendario WHERE ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, eventID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        titleLabel.setText(resultSet.getString("Titulo"));
                        startDateLabel.setText(resultSet.getDate("Data").toString());
                        endDateLabel.setText(resultSet.getDate("datafim").toString());
                        detailsLabel.setText(resultSet.getString("detalhes"));
                    } else {
                        System.out.println("No data found for event ID: " + eventID);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
