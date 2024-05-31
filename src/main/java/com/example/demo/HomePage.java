package com.example.demo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class HomePage extends JPanel {

    private int userId;

    public HomePage(int userId) {
        this.userId = userId;
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 20, 10);

        JLabel welcomeLabel = new JLabel("Olá, " + getUsername(userId)); // Displaying username
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.LINE_START;

        JPanel ongoingEventsPanel = createEventsPanel("Eventos em adamento");
        add(ongoingEventsPanel, gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_END;

        JPanel startingSoonEventsPanel = createEventsPanel("Eventos por começar");
        add(startingSoonEventsPanel, gbc);

        loadOngoingEvents(ongoingEventsPanel);
        loadStartingSoonEvents(startingSoonEventsPanel);
    }

    private JPanel createEventsPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel eventsLabel = new JLabel("<html><body style='width: 300px;'>Loading...</body></html>");
        eventsLabel.setVerticalAlignment(SwingConstants.TOP);
        eventsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(eventsLabel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private String getUsername(int userId) {
        String username = "";
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT nome FROM utilizadores WHERE ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        username = resultSet.getString("nome");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    private void loadOngoingEvents(JPanel panel) {
        JLabel eventsLabel = (JLabel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        StringBuilder events = new StringBuilder("<html><body style='width: 300px;'>");
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT Titulo, Data, datafim FROM calendario WHERE IDutilizador = ? AND Data <= ? AND datafim >= ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                statement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        events.append("<b>Titulo:</b> ").append(resultSet.getString("Titulo")).append("<br>");
                        events.append("<b>Data de inico:</b> ").append(resultSet.getDate("Data")).append("<br>");
                        events.append("<b>Data de fim:</b> ").append(resultSet.getDate("datafim")).append("<br><br>");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            events.append("Error loading ongoing events.");
        }
        events.append("</body></html>");
        eventsLabel.setText(events.toString());
    }
    
    private void loadStartingSoonEvents(JPanel panel) {
        JLabel eventsLabel = (JLabel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        StringBuilder events = new StringBuilder("<html><body style='width: 300px;'>");
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT Titulo, Data, datafim FROM calendario WHERE IDutilizador = ? AND Data > ? AND Data <= ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                statement.setDate(3, java.sql.Date.valueOf(LocalDate.now().plusDays(7))); // Events starting within 7 days
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        events.append("<b>Titulo:</b> ").append(resultSet.getString("Titulo")).append("<br>");
                        events.append("<b>Data de inicio:</b> ").append(resultSet.getDate("Data")).append("<br>");
                        events.append("<b>Data de fim:</b> ").append(resultSet.getDate("datafim")).append("<br><br>");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            events.append("Error loading starting soon events.");
        }
        events.append("</body></html>");
        eventsLabel.setText(events.toString());
    }
}
