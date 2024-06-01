package com.example.demo;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

public class ModifyEventDialog extends JDialog {

    private int eventID;
    private JTextField titleField;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JTextArea detailsArea;

    public ModifyEventDialog(int userId, int eventID) {
        this.eventID = eventID;

        setTitle("Alterar Evento");
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        loadEventData();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        panel.add(new JLabel("Titulo"));
        titleField = new JTextField();
        panel.add(titleField);

        panel.add(new JLabel("Data Inicio:"));
        startDateChooser = new JDateChooser();
        panel.add(startDateChooser);

        panel.add(new JLabel("Data fim:"));
        endDateChooser = new JDateChooser();
        panel.add(endDateChooser);

        panel.add(new JLabel("Detalhes"));
        detailsArea = new JTextArea();
        panel.add(new JScrollPane(detailsArea));

        JButton saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> saveEventData());
        panel.add(saveButton);

        add(panel);
    }

    private void loadEventData() {
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT Titulo, Data, datafim, detalhes FROM calendario WHERE ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, eventID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        titleField.setText(resultSet.getString("Titulo"));
                        startDateChooser.setDate(resultSet.getDate("Data"));
                        endDateChooser.setDate(resultSet.getDate("datafim"));
                        detailsArea.setText(resultSet.getString("detalhes"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveEventData() {
        // Retrieve edited values from text fields and text area
        String title = titleField.getText();
        LocalDate startDate = LocalDate.ofInstant(startDateChooser.getDate().toInstant(), startDateChooser.getCalendar().getTimeZone().toZoneId());
        LocalDate endDate = LocalDate.ofInstant(endDateChooser.getDate().toInstant(), endDateChooser.getCalendar().getTimeZone().toZoneId());
        String details = detailsArea.getText();

        // Update the event in the database
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "UPDATE calendario SET Titulo = ?, Data = ?, datafim = ?, detalhes = ? WHERE ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, title);
                statement.setDate(2, Date.valueOf(startDate));
                statement.setDate(3, Date.valueOf(endDate));
                statement.setString(4, details);
                statement.setInt(5, eventID);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close the dialog after saving
        dispose();
    }
}
