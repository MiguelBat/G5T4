package com.example.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CalendarPanel extends JPanel {

    private YearMonth currentYearMonth;
    private int userId;

    // Constructor with userId parameter
    public CalendarPanel(int userId) {
        this.userId = userId;
        currentYearMonth = YearMonth.now();
        setLayout(new BorderLayout());
        displayCalendar(currentYearMonth);
    }

    private void displayCalendar(YearMonth yearMonth) {
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        // Create header panel to display month and year
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel monthYearLabel = new JLabel(yearMonth.getMonth().toString() + " " + yearMonth.getYear());
        JButton previousButton = new JButton("<<");
        previousButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });
        JButton nextButton = new JButton(">>");
        nextButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });
        headerPanel.add(previousButton);
        headerPanel.add(monthYearLabel);
        headerPanel.add(nextButton);
    
        // Create calendar panel to display days
        JPanel daysPanel = new JPanel(new GridLayout(0, 7));
        String[] daysOfWeek = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sei", "Sab"};
        for (String day : daysOfWeek) {
            daysPanel.add(new JLabel(day, SwingConstants.CENTER));
        }
        int firstDayOfMonth = yearMonth.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < firstDayOfMonth; i++) {
            daysPanel.add(new JLabel());
        }
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day);
            JButton dayButton = new JButton(Integer.toString(day));
            List<String> events = getEventsForDay(currentDate);
            if (!events.isEmpty()) {
                dayButton.setBackground(Color.YELLOW);
            } else {
                dayButton.setBackground(Color.GREEN);
            }
            dayButton.addActionListener(e -> {
                displayEventsForDay(currentDate);
            });
            daysPanel.add(dayButton);
        }
        removeAll();
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(daysPanel, BorderLayout.CENTER);
        add(calendarPanel);
        revalidate();
        repaint();
    }

    private void updateCalendar() {
        removeAll();
        displayCalendar(currentYearMonth);
    }
    
    private List<String> getEventsForDay(LocalDate date) {
        List<String> events = new ArrayList<>();
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT Titulo FROM calendario WHERE IDutilizador = ? AND ? BETWEEN Data AND datafim";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setDate(2, Date.valueOf(date));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        events.add(resultSet.getString("Titulo"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    
    private void displayEventsForDay(LocalDate date) {
        List<String> events = getEventsForDay(date);
        JPanel eventPanel = new JPanel(new GridLayout(events.size() + 2, 1)); 
        for (String event : events) {
            JPanel eventRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            eventRow.add(new JLabel(event));
            JButton deleteButton = new JButton("Apagar");
            JButton modifyButton = new JButton("Modificar");
            JButton detailsButton = new JButton("Detalhes");
            deleteButton.addActionListener(evt -> {
                int choice = JOptionPane.showConfirmDialog(this, "Tem a certeza que quer apagar este evento?");
                if (choice == JOptionPane.YES_OPTION) {
                    // Get the ID of the event from the Calendario table
                    int eventID = getEventIDForDateAndTitle(date, event); 
            
                    // Open DeleteDayDialog passing the userId and eventID
                    openDeleteDayDialog(userId, eventID);
                
                }
            });
            
            modifyButton.addActionListener(evt -> {
                int eventID = getEventIDForDateAndTitle(date, event); // Implement this method

                openModifyDayDialog(userId, eventID);
            });
            detailsButton.addActionListener(evt -> {

                int eventID = getEventIDForDateAndTitle(date, event); // Implement this method
            
                    // Open DeleteDayDialog passing the userId and eventID
                    openDetailsDialog(userId, eventID);
            
            });
            eventRow.add(deleteButton);
            eventRow.add(modifyButton);
            eventRow.add(detailsButton);
            eventPanel.add(eventRow);
        }
        JButton addEventButton = new JButton("Adicionar evento");
        addEventButton.addActionListener(evt -> {
            openEditDayDialog(date);
        });
        eventPanel.add(addEventButton);
    
        JOptionPane.showMessageDialog(this, eventPanel, "Eventos para" + date, JOptionPane.PLAIN_MESSAGE);
    }

    private void openEditDayDialog(LocalDate date) {
        EditDayDialog editDayDialog = new EditDayDialog(date, userId);
        editDayDialog.setLocationRelativeTo(this); // Center the dialog relative to the calendar panel
        editDayDialog.setModal(true); // Set the dialog as modal
        editDayDialog.setVisible(true);
    }
    
    private int getEventIDForDateAndTitle(LocalDate date, String eventTitle) {
        int eventID = -1; // Default value if event is not found
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "SELECT ID FROM calendario WHERE IDutilizador = ? AND ? BETWEEN Data AND datafim AND Titulo = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setDate(2, Date.valueOf(date));
                statement.setString(3, eventTitle);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        eventID = resultSet.getInt("ID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Event ID for date " + date + " and title " + eventTitle + ": " + eventID);
        return eventID;
    }
    

    
    private void openDeleteDayDialog(int userId, int eventID) {
        DeleteDayDialog deleteDayDialog = new DeleteDayDialog(userId, eventID);
        deleteDayDialog.setLocationRelativeTo(this); // Center the dialog relative to the calendar panel
        deleteDayDialog.setModal(true); // set deleteDayDialog to modal
        deleteDayDialog.setVisible(true);
    }
    
    private void openModifyDayDialog(int userId, int eventID){
        ModifyEventDialog modifyEventDialog = new ModifyEventDialog(userId, eventID);
        modifyEventDialog.setLocationRelativeTo(this);
        modifyEventDialog.setModal(true);
        modifyEventDialog.setVisible(true);
    }

    private void openDetailsDialog(int userId, int eventID){
        DetailsDayDialog detailsEventDialog = new DetailsDayDialog(eventID, userId);
        detailsEventDialog.setLocationRelativeTo(this);
        detailsEventDialog.setModal(true);
        detailsEventDialog.setVisible(true);
    }
    
}
    