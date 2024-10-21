package com.restaurant.proj.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class OrderManagementUI {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JComboBox<String> staffComboBox; // Dropdown for staff names

    public OrderManagementUI() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Order Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE to prevent closing MainApp
        frame.setSize(600, 400);

        orderTableModel = new DefaultTableModel(new Object[]{"Order ID", "Customer Name", "Customer Phone", "Staff ID"}, 0);
        orderTable = new JTable(orderTableModel);
        loadOrderData(); // Load initial data

        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    Integer staffId = (Integer) orderTableModel.getValueAt(selectedRow, 3);
                    loadStaffData(staffId); // Load staff data for dropdown
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void loadOrderData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT o.Order_ID, c.Name AS Customer_Name, c.Phone_Number AS Customer_Phone, o.Staff_ID FROM Orders o JOIN Customer c ON o.Customer_ID = c.Customer_ID")) {

            orderTableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                int orderId = rs.getInt("Order_ID");
                String customerName = rs.getString("Customer_Name");
                String customerPhone = rs.getString("Customer_Phone");
                Integer staffId = rs.getInt("Staff_ID"); // Fetch Staff ID
                orderTableModel.addRow(new Object[]{orderId, customerName, customerPhone, staffId}); // Add rows to the table model
            }
            System.out.println("Executing query to load orders...");
            System.out.println("Number of orders fetched: " + orderTableModel.getRowCount());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading orders: " + e.getMessage());
        }
    }

    private void loadStaffData(Integer selectedStaffId) {
        staffComboBox = new JComboBox<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Staff_ID, Name FROM Staff")) {

            while (rs.next()) {
                int staffId = rs.getInt("Staff_ID");
                String staffName = rs.getString("Name");
                staffComboBox.addItem(staffId + " - " + staffName); // Display staff ID and name
            }

            if (selectedStaffId != null) {
                staffComboBox.setSelectedItem(selectedStaffId + " - " + getStaffName(selectedStaffId)); // Set selected item
            }

            // Show the dropdown in a custom dialog with OK button
            JPanel panel = new JPanel(new FlowLayout());
            panel.add(staffComboBox);
            int option = JOptionPane.showConfirmDialog(null, panel, "Select Staff ID", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                String selectedItem = (String) staffComboBox.getSelectedItem();
                if (selectedItem != null) {
                    int staffId = Integer.parseInt(selectedItem.split(" - ")[0]); // Get selected Staff ID
                    updateStaffId(staffId); // Update Staff ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading staff data: " + e.getMessage());
        }
    }

    private String getStaffName(Integer staffId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT Name FROM Staff WHERE Staff_ID = ?")) {
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }

    private void updateStaffId(int staffId) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an order to update.");
            return;
        }

        int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE Orders SET Staff_ID = ? WHERE Order_ID = ?")) {

            pstmt.setInt(1, staffId); // Update selected Staff ID
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
            loadOrderData(); // Reload order data
            JOptionPane.showMessageDialog(null, "Staff ID updated successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating Staff ID: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OrderManagementUI::new);
    }
}
