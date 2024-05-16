package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import com.example.demo.EditDayDialog;

public class CalendarPanel extends JPanel {

    

    private YearMonth currentYearMonth;


    
    public CalendarPanel() {
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
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : daysOfWeek) {
            daysPanel.add(new JLabel(day, SwingConstants.CENTER));
        }
        int firstDayOfMonth = yearMonth.atDay(1).getDayOfWeek().getValue();
        for (int i = 0; i < firstDayOfMonth; i++) {
            daysPanel.add(new JLabel());
        }
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = new JButton(Integer.toString(day));
            int finalDay = day;
            dayButton.addActionListener(e -> {
                LocalDate selectedDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), finalDay);
                List<String> events = getEventsForDay(selectedDate);
                if (!events.isEmpty()) {
                    StringBuilder message = new StringBuilder("Events for " + selectedDate + ":\n");
                    for (String event : events) {
                        message.append("- ").append(event).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, message.toString());
                } else {
                    int choice = JOptionPane.showConfirmDialog(this, "Nothing on this day. Do you want to add an event?");
                    if (choice == JOptionPane.YES_OPTION) {
                        // Open edit day dialog
                        openEditDayDialog(selectedDate);
                    }
                }
            });
            daysPanel.add(dayButton);
        }

        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(daysPanel, BorderLayout.CENTER);

        removeAll();
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
            String query = "SELECT Titulo FROM calendario WHERE Data = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDate(1, Date.valueOf(date));
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
    
    private boolean insertEvent(LocalDate date, String title, String details) {
        try (Connection connection = new DatabaseConnection().getConnection()) {
            String query = "INSERT INTO calendario (Data, Datacriada, Titulo, Detalhes) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDate(1, Date.valueOf(date));
                statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(3, title);
                statement.setString(4, details);
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Calendar");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new CalendarPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    private void openEditDayDialog(LocalDate date) {
        // Create and display the edit day dialog
        
        EditDayDialog editDayDialog = new EditDayDialog(date);
        editDayDialog.setLocationRelativeTo(this); // Center the dialog relative to the calendar panel
        editDayDialog.setVisible(true);
    }

}
