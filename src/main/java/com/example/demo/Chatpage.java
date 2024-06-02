package com.example.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class Chatpage extends JPanel {

    private int userId;
    private int recipientId;
    private JEditorPane chatArea;
    private JTextField messageField;
    private JTextField searchField;
    private JList<String> chatList;
    private DefaultListModel<String> chatListModel;
    private JButton sendCalendarButton;

    public Chatpage(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());
        initComponents();
        loadChatList();
    }

    private void initComponents() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, getHeight()));
    
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Pesquisar");
        searchButton.addActionListener(e -> searchUsers());
    
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        leftPanel.add(searchPanel, BorderLayout.NORTH);
    
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.addListSelectionListener(e -> selectChat());
        leftPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);
    
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 20, 10);
    
        JLabel titleLabel = new JLabel("Chat");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(titleLabel, gbc);
    
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
    
        chatArea = new JEditorPane();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        rightPanel.add(scrollPane, gbc);
    
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        messageField = new JTextField();
        rightPanel.add(messageField, gbc);
    
        gbc.gridx++;
        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(e -> sendMessage());
        rightPanel.add(sendButton, gbc);
    
        gbc.gridx++;
        sendCalendarButton = new JButton("Eventos");
        sendCalendarButton.addActionListener(e -> openEventSelectionWindow());
        rightPanel.add(sendCalendarButton, gbc);
    
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    

    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            return;
        }
        chatListModel.clear(); // Limpar resultados de pesquisa anteriores
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT ID, nome FROM utilizadores WHERE LOWER(nome) LIKE ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + searchTerm.toLowerCase() + "%");
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        chatListModel.addElement(resultSet.getString("ID") + " - " + resultSet.getString("nome"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadChatList() {
        chatListModel.clear();
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT DISTINCT IDrecipiente, IDautor FROM chatlogs WHERE IDautor = ? OR IDrecipiente = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setInt(2, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id1 = resultSet.getInt("IDrecipiente");
                        int id2 = resultSet.getInt("IDautor");
                        int chatPartnerId = (id1 == userId) ? id2 : id1;
                        chatListModel.addElement(chatPartnerId + " - " + getUsername(chatPartnerId));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectChat() {
        String selectedChat = chatList.getSelectedValue();
        if (selectedChat != null) {
            recipientId = Integer.parseInt(selectedChat.split(" - ")[0]);
            loadChatHistory();
        }
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

    private void loadChatHistory() {
        if (recipientId == 0) {
            chatArea.setText("Selecione um destinatário para começar a conversar.");
            return;
        }
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT IDautor, menssagem, dataenviada FROM chatlogs WHERE (IDautor = ? AND IDrecipiente = ?) OR (IDautor = ? AND IDrecipiente = ?) ORDER BY dataenviada";
            System.out.println("Query: " + query);
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setInt(2, recipientId);
                statement.setInt(3, recipientId);
                statement.setInt(4, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    StringBuilder chatHistory = new StringBuilder();
                    while (resultSet.next()) {
                        String sender = resultSet.getInt("IDautor") == userId ? "Você" : getUsername(resultSet.getInt("IDautor"));
                        LocalDateTime sentTime = resultSet.getTimestamp("dataenviada").toLocalDateTime();
                        if (sentTime.toLocalDate().isBefore(LocalDate.now())) {
                            continue; // Ignorar mensagens anteriores
                        }
                        chatHistory.append(sender).append(" (").append(formatDateTime(sentTime)).append("):\n");
                        String message = resultSet.getString("menssagem");
                        chatHistory.append(message).append("\n\n");
                    }
                    chatArea.setText(chatHistory.toString());
                }
            }
                } catch (SQLException e) {
                        e.printStackTrace();
                        chatArea.setText("Erro ao carregar o histórico do chat.");
                        }
                }
                private String formatDateTime(LocalDateTime dateTime) {
                    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                
                private void sendMessage() {
                    String message = messageField.getText().trim();
                    if (message.isEmpty() || recipientId == 0) {
                        return;
                    }
                    try (Connection connection = new DatabaseConnection().getConnection()) {
                        String query = "INSERT INTO chatlogs (IDautor, IDrecipiente, menssagem, dataenviada, metadata) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement statement = connection.prepareStatement(query)) {
                            statement.setInt(1, userId);
                            statement.setInt(2, recipientId);
                            statement.setString(3, message);
                            statement.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                            statement.setString(5, ""); // Inserir metadados vazios ou qualquer valor padrão
                            statement.executeUpdate();
                            messageField.setText("");
                            loadChatHistory(); // Atualizar o histórico do chat
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                
                private void openEventSelectionWindow() {
                    // Criar e configurar a janela de diálogo
                    JDialog eventSelectionDialog = new JDialog();
                    eventSelectionDialog.setTitle("Escolher Eventos");
                    eventSelectionDialog.setSize(400, 300);
                    eventSelectionDialog.setLocationRelativeTo(this);
                    eventSelectionDialog.setLayout(new BorderLayout());
                
                    // Modelo de lista para os eventos
                    DefaultListModel<String> eventListModel = new DefaultListModel<>();
                    // Aqui você pode adicionar lógica para carregar os eventos do seu sistema
                    // Por exemplo, recuperar os eventos do banco de dados
                    List<String> events = getEventsFromDatabase(); // Método fictício para recuperar os eventos do banco de dados
                    for (String event : events) {
                        eventListModel.addElement(event);
                    }
                
                    JList<String> eventList = new JList<>(eventListModel);
                    eventList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                
                    JScrollPane scrollPane = new JScrollPane(eventList);
                    eventSelectionDialog.add(scrollPane, BorderLayout.CENTER);
                
                    // Adicionar um botão para confirmar a seleção de eventos
                    JButton confirmButton = new JButton("Enviar Eventos Selecionados");
                    confirmButton.addActionListener(e -> {
                        // Obter os eventos selecionados
                        List<String> selectedEvents = eventList.getSelectedValuesList();
                        // Enviar os eventos selecionados
                        sendSelectedEvents(selectedEvents);
                        // Fechar a janela de diálogo
                        eventSelectionDialog.dispose();
                    });
                    eventSelectionDialog.add(confirmButton, BorderLayout.SOUTH);
                
                    // Tornar a janela de diálogo visível
                    eventSelectionDialog.setVisible(true);
                }
                
                private void sendSelectedEvents(List<String> selectedEvents) {
                    // Verificar se algum evento foi selecionado
                    if (selectedEvents.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Eventos não selecionados", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                
                    // Iterar sobre os eventos selecionados
                    for (String event : selectedEvents) {
                        // Obter os detalhes do evento atual do banco de dados
                        String[] eventDetails = getEventDetailsFromDatabase(event);
                        // Formatar os detalhes do evento
                        String eventMessage = createEventMessage(eventDetails[0], eventDetails[1], eventDetails[2], eventDetails[3]);
                        // Enviar a mensagem do evento
                        sendEventMessage(eventMessage);
                    }
                }

                private String[] getEventDetailsFromDatabase(String eventTitle) {
                    String[] eventDetails = new String[4]; // Array para armazenar os detalhes do evento
                    try (Connection connection = new DatabaseConnection().getConnection()) {
                        String query = "SELECT Data, datafim, Detalhes FROM calendario WHERE Titulo = ?";
                        try (PreparedStatement statement = connection.prepareStatement(query)) {
                            statement.setString(1, eventTitle);
                            try (ResultSet resultSet = statement.executeQuery()) {
                                if (resultSet.next()) {
                                    // Recuperar os detalhes do evento do resultado da consulta
                                    eventDetails[0] = eventTitle; // Título do evento
                                    eventDetails[1] = resultSet.getString("Data"); // Data de início do evento
                                    eventDetails[2] = resultSet.getString("datafim"); // Data de término do eventor
                                    eventDetails[3] = resultSet.getString("Detalhes"); // Detalhes do evento
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return eventDetails;
                }
                
                
                
                private List<String> getEventsFromDatabase() {
                    // Implemente a lógica para recuperar todos os eventos do banco de dados
                    List<String> events = new ArrayList<>();
                    try (Connection connection = new DatabaseConnection().getConnection()) {
                        String query = "SELECT Titulo FROM calendario";
                        try (PreparedStatement statement = connection.prepareStatement(query);
                             ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                events.add(resultSet.getString("Titulo"));
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return events;
                }
                
                private void sendEventMessage(String message) {
                    if (message.isEmpty() || recipientId == 0) {
                        return;
                    }
                    try (Connection connection = new DatabaseConnection().getConnection()) {
                        String query = "INSERT INTO chatlogs (IDautor, IDrecipiente, menssagem, dataenviada, metadata) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement statement = connection.prepareStatement(query)) {
                            statement.setInt(1, userId);
                            statement.setInt(2, recipientId);
                            statement.setString(3, message);
                            statement.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                            statement.setString(5, ""); // Inserir metadados vazios ou qualquer valor padrão
                            statement.executeUpdate();
                            loadChatHistory(); // Atualizar o histórico do chat
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                
                private String createEventMessage(String titulo, String data, String dataFim, String detalhes) {
                    StringBuilder eventMessage = new StringBuilder();
                    eventMessage.append("Evento: ").append(titulo).append("\n");
                    eventMessage.append("Data: ").append(data).append("\n");
                    eventMessage.append("Data de fim: ").append(dataFim).append("\n");
                    eventMessage.append("Detalhes: ").append(detalhes).append("\n");
                    return eventMessage.toString();
                }
            }                
