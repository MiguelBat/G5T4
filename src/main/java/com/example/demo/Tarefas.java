package com.example.demo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Tarefas extends JPanel {
    private int userId;
    private DatabaseConnection dbConnection;

    public Tarefas(int userId) {
        this.userId = userId;
        this.dbConnection = new DatabaseConnection();
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 20, 10);

        JLabel titleLabel = new JLabel("Tarefas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.LINE_START;

        JPanel ongoingTasksPanel = createTasksPanel("Tarefas concluídas", "Concluído");
        add(ongoingTasksPanel, gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_END;

        JPanel startingSoonTasksPanel = createTasksPanel("Tarefas por acabar", "Por começar", "Em progresso");
        add(startingSoonTasksPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton addButton = new JButton("Adicionar Tarefa");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTarefa addTarefa = new AddTarefa(userId, (JFrame) SwingUtilities.getWindowAncestor(Tarefas.this));
                addTarefa.setVisible(true);
            }
        });

        add(addButton, gbc);

        setVisible(true);
    }

    private JPanel createTasksPanel(String title, String... estados) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
    
        List<TaskDetails> tasks = fetchTasksDetails(estados);
    
        JPanel taskDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        for (TaskDetails task : tasks) {
            JLabel taskLabel = new JLabel("<html><b>" + task.getTitle() + "</b><br>Data Início: " + task.getDataInicio() +
                    "<br>Data Fim: " + task.getDataFim() + "</html>");
            gbc.gridy++;
            taskDetailsPanel.add(taskLabel, gbc);
    
            JButton detalhesButton = new JButton("Detalhes");
            detalhesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openTarefaDetalhes(task.getTarefaId());
                }
            });
            gbc.gridx++;
            taskDetailsPanel.add(detalhesButton, gbc);
        }
    
        JScrollPane scrollPane = new JScrollPane(taskDetailsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        return panel;
    }
    
    private List<TaskDetails> fetchTasksDetails(String... estados) {
        List<TaskDetails> tasks = new ArrayList<>();
        Connection connection = dbConnection.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
    
        try {
            String sql = "SELECT ID, titulo, datainicio, datafim FROM Tarefas WHERE IDautor = ? AND estado IN (" + repeat("?", estados.length, ",") + ")";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            for (int i = 0; i < estados.length; i++) {
                preparedStatement.setString(i + 2, estados[i]);
            }
    
            resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                int tarefaId = resultSet.getInt("ID");
                String titulo = resultSet.getString("titulo");
                String dataInicio = resultSet.getString("datainicio");
                String dataFim = resultSet.getString("datafim");
                tasks.add(new TaskDetails(tarefaId, titulo, dataInicio, dataFim));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    
        return tasks;
    }
    
    // Define a TaskDetails class to hold task details
    private class TaskDetails {
        private int tarefaId;
        private String title;
        private String dataInicio;
        private String dataFim;
    
        public TaskDetails(int tarefaId, String title, String dataInicio, String dataFim) {
            this.tarefaId = tarefaId;
            this.title = title;
            this.dataInicio = dataInicio;
            this.dataFim = dataFim;
        }
    
        public int getTarefaId() {
            return tarefaId;
        }
    
        public String getTitle() {
            return title;
        }
    
        public String getDataInicio() {
            return dataInicio;
        }
    
        public String getDataFim() {
            return dataFim;
        }
    }

    private void openTarefaDetalhes(int tarefaId) {
        // Create and display TarefaDetalhes with the provided tarefaId
        TarefaDetalhes detalhesFrame = new TarefaDetalhes(tarefaId, dbConnection.getConnection());
        detalhesFrame.setVisible(true);
    }
    

    private String repeat(String str, int count, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(str);
        }
        return sb.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dbConnection.closeConnection();
    }
}
