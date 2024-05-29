package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
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
                List<String> dayEvents = getEventsForDay(currentDate);
                if (!dayEvents.isEmpty()) {
                    StringBuilder message = new StringBuilder("Events for " + currentDate + ":\n");
                    for (String event : dayEvents) {
                        message.append("- ").append(event).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, message.toString());
                } else {
                    int choice = JOptionPane.showConfirmDialog(this, "Nothing on this day. Do you want to add an event?");
                    if (choice == JOptionPane.YES_OPTION) {
                        // Open edit day dialog
                        openEditDayDialog(currentDate);
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

   
    
    private int getUserIdFromDemoApplication() {
        DemoApplication demoApp = DemoApplication.getInstance(userId); // Assuming getInstance() returns the instance
        return demoApp.getUserId();
    }
    

    private void openEditDayDialog(LocalDate date) {
        int userId = getUserIdFromDemoApplication();
        EditDayDialog editDayDialog = new EditDayDialog(date, userId);
        editDayDialog.setLocationRelativeTo(this); // Center the dialog relative to the calendar panel
        editDayDialog.setVisible(true);     
        System.out.println(userId);
    }


}
