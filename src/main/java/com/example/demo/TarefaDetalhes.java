package com.example.demo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TarefaDetalhes extends JFrame {
    private int tarefaId;
    private String titulo;
    private String detalhes;
    private String estado;
    private byte[] midia;
    private String nomeMidia;
    private String tipoMidia;
    private String dataInicio;
    private String dataFim;


    // UI Components
    private JLabel titleLabel;
    private JLabel detalhesLabel;
    private JComboBox<String> estadoComboBox;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton downloadButton;

    public TarefaDetalhes(int tarefaId, Connection connection) {
        this.tarefaId = tarefaId;
        fetchDataFromDatabase(connection);
        initializeUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Detalhes da Tarefa: " + titulo);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchDataFromDatabase(Connection connection) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Prepare SQL query
            String sql = "SELECT titulo, detalhes, estado, midia, nomemidia, tipomidia, datainicio, datafim FROM Tarefas WHERE ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, tarefaId);

            // Execute query
            resultSet = preparedStatement.executeQuery();

            // Process result set
            if (resultSet.next()) {
                titulo = resultSet.getString("titulo");
                detalhes = resultSet.getString("detalhes");
                estado = resultSet.getString("estado");
                midia = resultSet.getBytes("midia");
                nomeMidia = resultSet.getString("nomemidia");
                tipoMidia = resultSet.getString("tipomidia");
                dataInicio = resultSet.getString("datainicio");
                dataFim = resultSet.getString("datafim");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        titleLabel = new JLabel("Título: " + titulo);
        detalhesLabel = new JLabel("Detalhes: " + detalhes);
        estadoComboBox = new JComboBox<>(new String[]{"Em Progresso", "Concluído", "Por começar"});
        estadoComboBox.setSelectedItem(estado);
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadMidia();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));

        buttonPanel.add(downloadButton);

        saveButton = new JButton("Salvar");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });
        buttonPanel.add(saveButton);

        deleteButton = new JButton("Apagar");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTarefa(tarefaId); // Pass the tarefaId to the deleteTarefa method
            }
        });
        buttonPanel.add(deleteButton);

        JPanel detailsPanel = new JPanel(new GridLayout(6, 1));
        detailsPanel.add(titleLabel);
        detailsPanel.add(detalhesLabel);
        detailsPanel.add(estadoComboBox);

        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    private void downloadMidia() {
        if (midia != null && nomeMidia != null && tipoMidia != null) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File saveLocation = fileChooser.getSelectedFile();
                String fileName = JOptionPane.showInputDialog(this, "Enter file name (without extension):");
                if (fileName != null && !fileName.trim().isEmpty()) {
                    // Retrieve extension from tipoMidia
                    String extension = getFileExtensionFromTipoMidia();
                    if (extension != null) {
                        fileName += extension; // Append the extension to the file name
                    } else {
                        JOptionPane.showMessageDialog(this, "Error retrieving file extension.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // Exit method if extension retrieval fails
                    }
                    // Ensure that the file name doesn't contain invalid characters
                    fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_"); // Replace invalid characters with underscores
                    File file = new File(saveLocation.getParentFile(), fileName); // Get parent directory and use full file name
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(midia);
                        JOptionPane.showMessageDialog(this, "File downloaded successfully!");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error downloading file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid file name.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file available for download.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getFileExtensionFromTipoMidia() {
        if (tipoMidia.equals("jpeg")) {
            return ".jpg";
        } else if (tipoMidia.equals("png")) {
            return ".png";
        } else if (tipoMidia.equals("pdf")) {
            return ".pdf";
        } else if (tipoMidia.equals("docx")) {
            return ".docx";
        } else if (tipoMidia.equals("mp4")) {
            return ".mp4";
        } else if (tipoMidia.equals("mp3")) {
            return ".mp3";
        } else if (tipoMidia.equals("txt")) {
            return ".txt";
        }
        // Add more extensions if needed
        return null; // Return null if extension not found or unsupported
    }


    private void saveChanges() {
        estado = estadoComboBox.getSelectedItem().toString();
        
        // Update the estado of the tarefa in the database
        try (Connection connection = new DatabaseConnection().getConnection()) {
            if (connection != null) {
                String query = "UPDATE Tarefas SET estado = ? WHERE ID = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, estado);
                    statement.setInt(2, tarefaId);
                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Tarefa atulizada");
                    } else {
                        JOptionPane.showMessageDialog(this, "Erro a atulizar a tarefa", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating tarefa.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteTarefa(int tarefaId) {
        try (Connection connection = new DatabaseConnection().getConnection()) {
            // Ensure connection is not null
            if (connection != null && tarefaId != 0) { 
                String query = "DELETE FROM Tarefas WHERE ID = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, tarefaId);
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Tarefa deleted successfully!");
                        dispose(); // Close the window
                    } else {
                        JOptionPane.showMessageDialog(this, "Tarefa not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid tarefa ID or connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting tarefa.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

}
