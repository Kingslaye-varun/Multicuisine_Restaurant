package com.restaurant.proj.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

import com.microsoft.sqlserver.jdbc.SQLServerException; 

public class TableManagementUI {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    private JTextField tableNumberField;
    private JTextField capacityField;
    private JTable tableView;
    private DefaultTableModel tableModel;

    public TableManagementUI() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Table Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE to prevent closing MainApp
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new BorderLayout());

        // Table model and view
        tableModel = new DefaultTableModel(new Object[]{"Table ID", "Table Number", "Capacity", "Reserved"}, 0);
        tableView = new JTable(tableModel);
        loadTableData(); // Load initial data

        // Panel for adding/editing table information
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Table Number:"));
        tableNumberField = new JTextField();
        inputPanel.add(tableNumberField);

        inputPanel.add(new JLabel("Capacity:"));
        capacityField = new JTextField();
        inputPanel.add(capacityField);

        JButton addButton = new JButton("Add Table");
        addButton.addActionListener(this::addTable);

        JButton updateButton = new JButton("Update Table");
        updateButton.addActionListener(this::updateTable);

        JButton deleteButton = new JButton("Delete Table");
        deleteButton.addActionListener(this::deleteTable);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        panel.add(new JScrollPane(tableView), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void loadTableData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT rt.Table_ID, rt.Table_Number, rt.Capacity, " +
                "CASE WHEN r.Table_ID IS NOT NULL THEN 'Yes' ELSE 'No' END AS Reserved " +
                "FROM Restaurant_Table rt LEFT JOIN Reservation r ON rt.Table_ID = r.Table_ID")) {

            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                int tableId = rs.getInt("Table_ID");
                int tableNumber = rs.getInt("Table_Number");
                int capacity = rs.getInt("Capacity");
                String reserved = rs.getString("Reserved");
                tableModel.addRow(new Object[]{tableId, tableNumber, capacity, reserved}); // Add rows to the table model
            }

            // Hide the Table ID column
            tableView.getColumnModel().getColumn(0).setMinWidth(0);
            tableView.getColumnModel().getColumn(0).setMaxWidth(0);
            tableView.getColumnModel().getColumn(0).setPreferredWidth(0);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading table data: " + e.getMessage());
        }
    }

    private void addTable(ActionEvent e) {
        String tableNumberText = tableNumberField.getText();
        String capacityText = capacityField.getText();

        if (tableNumberText.isEmpty() || capacityText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Table Number and Capacity are required.");
            return;
        }

        // Check for existing table number
        if (isTableNumberExists(Integer.parseInt(tableNumberText))) {
            JOptionPane.showMessageDialog(null, "Table Number already exists. Please enter a unique Table Number.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Restaurant_Table (Table_Number, Capacity) VALUES (?, ?)")) {

            pstmt.setInt(1, Integer.parseInt(tableNumberText));
            pstmt.setInt(2, Integer.parseInt(capacityText));
            pstmt.executeUpdate();
            loadTableData(); // Reload table data
            JOptionPane.showMessageDialog(null, "Table added successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding table: " + ex.getMessage());
        }
    }

    private boolean isTableNumberExists(int tableNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM Restaurant_Table WHERE Table_Number = ?")) {
            pstmt.setInt(1, tableNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if table number exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateTable(ActionEvent e) {
        int selectedRow = tableView.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a table to update.");
            return;
        }

        int tableId = (int) tableModel.getValueAt(selectedRow, 0);
        String tableNumberText = tableNumberField.getText();
        String capacityText = capacityField.getText();

        if (tableNumberText.isEmpty() || capacityText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Table Number and Capacity are required.");
            return;
        }

//        // Check for existing table number if it is different
//        if (isTableNumberExists(Integer.parseInt(tableNumberText))) {
//            JOptionPane.showMessageDialog(null, "Table Number already exists. Please enter a unique Table Number.");
//            return;
        //}

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE Restaurant_Table SET Table_Number = ?, Capacity = ? WHERE Table_ID = ?")) {

            pstmt.setInt(1, Integer.parseInt(tableNumberText));
            pstmt.setInt(2, Integer.parseInt(capacityText));
            pstmt.setInt(3, tableId);
            pstmt.executeUpdate();
            loadTableData(); // Reload table data
            JOptionPane.showMessageDialog(null, "Table updated successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating table: " + ex.getMessage());
        }
    }

    private void deleteTable(ActionEvent e) {
        int selectedRow = tableView.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a table to delete.");
            return;
        }

        int tableId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this table?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Restaurant_Table WHERE Table_ID = ?")) {

                pstmt.setInt(1, tableId);
                pstmt.executeUpdate();
                loadTableData(); // Reload table data
                JOptionPane.showMessageDialog(null, "Table deleted successfully.");
            } catch (SQLServerException ex) {
                if (ex.getMessage().contains("FK__Reservati__Table__4CA06362")) {
                    JOptionPane.showMessageDialog(null, "Cannot remove table because it is reserved.");
                } else {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error deleting table: " + ex.getMessage());
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting table: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TableManagementUI::new);
    }
}