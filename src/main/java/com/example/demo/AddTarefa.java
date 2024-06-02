package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

// Import JDateChooser
import com.toedter.calendar.JDateChooser;

public class AddTarefa extends JDialog {
    private final int userId;
    private JTextField tituloField;
    private JTextArea detalhesArea;
    private JComboBox<String> estadoComboBox;
    private JLabel midiaLabel;
    private JTextField nomemidiaField;
    private JTextField tipomidiaField;
    private JDateChooser dataInicioPicker;
    private JDateChooser dataFimPicker;

    public AddTarefa(int userId, JFrame parent) {
        super(parent, "Adicionar Tarefa", true); // Set title and modality
        this.userId = userId;
        setSize(400, 400);
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        formPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx++;
        tituloField = new JTextField(20);
        formPanel.add(tituloField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Detalhes:"), gbc);
        gbc.gridx++;
        detalhesArea = new JTextArea(5, 20);
        JScrollPane detalhesScrollPane = new JScrollPane(detalhesArea);
        formPanel.add(detalhesScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx++;
        String[] estados = {"Por começar", "Concluído", "Em progresso"};
        estadoComboBox = new JComboBox<>(estados);
        formPanel.add(estadoComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        midiaLabel = new JLabel("Anexar arquivo:");
        formPanel.add(midiaLabel, gbc);
        gbc.gridx++;
        JButton midiaButton = new JButton("Selecionar arquivo");
        midiaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        formPanel.add(midiaButton, gbc);

        // Add fields for dataInicio, dataFim, nomemidia, and tipomidia
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Data de Início:"), gbc);
        gbc.gridx++;
        dataInicioPicker = new JDateChooser();
        formPanel.add(dataInicioPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Data de Fim:"), gbc);
        gbc.gridx++;
        dataFimPicker = new JDateChooser();
        formPanel.add(dataFimPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Nome do arquivo:"), gbc);
        gbc.gridx++;
        nomemidiaField = new JTextField(20);
        formPanel.add(nomemidiaField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Tipo do arquivo:"), gbc);
        gbc.gridx++;
        tipomidiaField = new JTextField(10);
        formPanel.add(tipomidiaField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button to add task
        JButton addButton = new JButton("Adicionar");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTarefaToDatabase();
            }
        });
        add(addButton, BorderLayout.SOUTH);
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.exists()) {
                nomemidiaField.setText(selectedFile.getAbsolutePath()); // Store absolute path
                tipomidiaField.setText(getFileType(selectedFile));
            } else {
                nomemidiaField.setText(""); // Clear the file path
                tipomidiaField.setText(""); // Clear the file type
                JOptionPane.showMessageDialog(this, "Arquivo não selecionado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


    private String getFileType(File file) {
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex != -1 && lastIndex < fileName.length() - 1) {
            return fileName.substring(lastIndex + 1);
        }
        return "";
    }

    private void addTarefaToDatabase() {
        // Get values from fields
        String titulo = tituloField.getText();
        String detalhes = detalhesArea.getText();
        String estado = estadoComboBox.getSelectedItem().toString();
        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = LocalDate.now(); // dataFimPicker.getDate()
        String nomemidia = nomemidiaField.getText();
        String tipomidia = tipomidiaField.getText();
    
        // Insert into database
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query;
            if (nomemidia != null && !nomemidia.isEmpty()) {
                query = "INSERT INTO tarefas (IDautor, titulo, detalhes, estado, midia, nomemidia, tipomidia, datainicio, datafim) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                query = "INSERT INTO tarefas (IDautor, titulo, detalhes, estado, datainicio, datafim) VALUES (?, ?, ?, ?, ?, ?)";
            }
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setString(2, titulo);
                statement.setString(3, detalhes);
                statement.setString(4, estado);
                if (nomemidia != null && !nomemidia.isEmpty()) {
                    File selectedFile = new File(nomemidia);
                    if (selectedFile.exists()) {
                        FileInputStream fis = new FileInputStream(selectedFile);
                        statement.setBinaryStream(5, fis, (int) selectedFile.length());
                        statement.setString(6, nomemidia);
                        statement.setString(7, tipomidia);
                        statement.setDate(8, java.sql.Date.valueOf(dataInicio));
                        statement.setDate(9, java.sql.Date.valueOf(dataFim));
                    }
                } else {
                    // No media file provided, set only the remaining parameters
                    statement.setDate(5, java.sql.Date.valueOf(dataInicio));
                    statement.setDate(6, java.sql.Date.valueOf(dataFim));
                }
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Tarefa adicionada com sucesso!");
                dispose(); // Close the dialog after successful addition
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao adicionar tarefa. Por favor, tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
}
