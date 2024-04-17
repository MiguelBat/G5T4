package com.example.demo;

import javax.swing.*;

import java.awt.*;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarPanel extends JPanel implements DarkModeListener {

    private YearMonth currentYearMonth;
    private boolean darkModeEnabled;

    public CalendarPanel() {
        currentYearMonth = YearMonth.now();
        darkModeEnabled = false; // Default to light mode
        setLayout(new BorderLayout());
        displayCalendar(currentYearMonth);
    }

    @SuppressWarnings("deprecation")
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
        String[] daysOfWeek = {"Seg", "Ter", "Qua", "Qui", "Sei", "Sab", "Dom"};
        for (String day : daysOfWeek) {
            daysPanel.add(new JLabel(day, SwingConstants.CENTER));
        }
        int firstDayOfMonth = yearMonth.atDay(1).getDayOfWeek().getValue(); // Adjusted to start from Sunday
        for (int i = 0; i < firstDayOfMonth; i++) {
            daysPanel.add(new JLabel());
        }
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = new JButton(Integer.toString(day));
            int finalDay = day;
            dayButton.addActionListener(e -> JOptionPane.showMessageDialog(this,"Selecionaste " + finalDay + " de " + yearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "PT")).substring(0, 1).toUpperCase() + yearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "PT")).substring(1).toLowerCase() + " de " + yearMonth.getYear()));daysPanel.add(dayButton);
        }

        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(daysPanel, BorderLayout.CENTER);

        // Apply dark or light mode colors
        if (darkModeEnabled) {
            applyDarkModeColors(calendarPanel, headerPanel, monthYearLabel, previousButton, nextButton, daysPanel);
        } else {
            applyLightModeColors(calendarPanel, headerPanel, monthYearLabel, previousButton, nextButton, daysPanel);
        }

        removeAll();
        add(calendarPanel);
        revalidate();
        repaint();
    }

    private void updateCalendar() {
        removeAll();
        displayCalendar(currentYearMonth);
    }

    private void applyDarkModeColors(Component... components) {
        for (Component component : components) {
            component.setForeground(Color.WHITE);
            if (component instanceof Container) {
                applyDarkModeColors(((Container) component).getComponents());
            }
        }
        setBackground(Color.BLACK);
    }

    private void applyLightModeColors(Component... components) {
        for (Component component : components) {
            component.setForeground(Color.BLACK);
            if (component instanceof Container) {
                applyLightModeColors(((Container) component).getComponents());
            }
        }
        setBackground(Color.WHITE);
    }

    public void toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled;
        updateCalendar();
    }

    @Override
    public void darkModeToggled(boolean darkModeEnabled) {
        this.darkModeEnabled = darkModeEnabled;
        updateCalendar();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Calendar Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CalendarPanel calendarPanel = new CalendarPanel();
        frame.getContentPane().add(calendarPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Toggle dark mode example (you can call this method based on user action)
        calendarPanel.toggleDarkMode();
    }
}
