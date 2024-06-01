package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;

import com.toedter.calendar.JDateChooser;

public class EditDayDialog extends JDialog {

    private int userId;

    private JTextField titleField;
    private JTextArea detailsArea;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;

    public EditDayDialog(LocalDate selectedDate, int userId) {
        this.userId = userId;

        setTitle("Editar evento");
        setSize(400, 400); // Increased size to accommodate all components
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this dialog
        setLocationRelativeTo(null); // Center the dialog on the screen

        // Create and add components to the dialog
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout for better organization

        // Start date picker
        startDateChooser = new JDateChooser();
        startDateChooser.setDate(java.sql.Date.valueOf(selectedDate));
        panel.add(new JLabel("Data de inicio:"));
        panel.add(startDateChooser);

        // End date picker
        endDateChooser = new JDateChooser();
        panel.add(new JLabel("Data do fim:"));
        panel.add(endDateChooser);

        // Title field
        panel.add(new JLabel("Titulo:"));
        titleField = new JTextField();
        panel.add(titleField);

        // Details area
        panel.add(new JLabel("Detalhes:"));
        detailsArea = new JTextArea();
        detailsArea.setRows(5); // Set preferred size for details area
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        panel.add(scrollPane);

        // Save button
        JButton saveButton = new JButton("Guardar");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEvent();
            }
        });
        panel.add(saveButton);

        add(panel);
    }

    private void saveEvent() {
        String title = titleField.getText();
        String details = detailsArea.getText();
    
        // Check if start date and end date choosers are not null
        if (startDateChooser.getDate() == null || endDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Porfavor escolha as duas datas");
            return; // Exit the method if either date is not selected
        }
    
        LocalDate startDate = startDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = endDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "INSERT INTO calendario (IDutilizador, Data, Datacriada, Titulo, Detalhes, datafim) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setDate(2, java.sql.Date.valueOf(startDate));
                statement.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(4, title);
                statement.setString(5, details);
                statement.setDate(6, java.sql.Date.valueOf(endDate));
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Guardado");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the event.");
        }
    }
}