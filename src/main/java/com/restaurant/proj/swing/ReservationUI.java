package com.restaurant.proj.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationUI {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    public ReservationUI() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Customer Reservation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(7, 2));

        // Input fields for customer details
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        // Dropdown for table capacity
        JLabel capacityLabel = new JLabel("Table Capacity:");
        String[] capacities = {"2", "3", "4", "5", "6"}; // Drop-down for capacities
        JComboBox<String> capacityDropdown = new JComboBox<>(capacities);

        // Total amount label
        JLabel totalLabel = new JLabel("Total: $0.00"); // Default value, will be updated later
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Calculate the total amount when the GUI is created
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            double totalAmount = calculateTotalAmount(conn);
            totalLabel.setText(String.format("Total: $%.2f", totalAmount)); // Update total amount label
        } catch (SQLException ex) {
            ex.printStackTrace();
            totalLabel.setText("Total: Error retrieving amount");
        }

        // Submit button
        JButton submitButton = new JButton("Reserve");

        submitButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            int requestedCapacity = Integer.parseInt((String) capacityDropdown.getSelectedItem());

            // Validate the input fields
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required.");
                return;
            }

            // Process the reservation
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                // Calculate the total amount left before creating the reservation
                double totalAmountLeft = calculateTotalAmount(conn);
                totalLabel.setText(String.format("Total: $%.2f", totalAmountLeft)); // Update total amount label

                // Step 1: Check if the customer already exists
                int customerId = findOrInsertCustomer(conn, name, phone, email);

                // Step 2: Find an available table with the requested or greater capacity
                int tableId = findAvailableTable(conn, requestedCapacity);

                if (tableId == -1) {
                    JOptionPane.showMessageDialog(frame, "No available tables found with the requested capacity.");
                    return;
                }

                // Step 3: Reserve the table for the customer
                createReservation(conn, customerId, tableId);

                // Show success message and close window
                JOptionPane.showMessageDialog(frame, "Reservation successful!");
                frame.dispose(); // Close the current reservation window

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error creating reservation: " + ex.getMessage());
            }
        });

        // Add components to panel
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(capacityLabel);
        panel.add(capacityDropdown);
        panel.add(totalLabel); // Add total amount label to the panel
        panel.add(submitButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Check if customer exists, otherwise insert and return the Customer_ID
    private int findOrInsertCustomer(Connection conn, String name, String phone, String email) throws SQLException {
        String selectCustomerSQL = "SELECT Customer_ID FROM Customer WHERE Name = ? AND Phone_Number = ? AND Email = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectCustomerSQL)) {
            selectStmt.setString(1, name);
            selectStmt.setString(2, phone);
            selectStmt.setString(3, email);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Customer_ID"); // Customer found, return existing Customer_ID
            } else {
                // If customer doesn't exist, insert the customer
                return insertCustomer(conn, name, phone, email);
            }
        }
    }

    // Insert a new customer and return the new Customer_ID
    private int insertCustomer(Connection conn, String name, String phone, String email) throws SQLException {
        String insertCustomerSQL = "INSERT INTO Customer (Name, Phone_Number, Email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, email);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return generated Customer_ID
            } else {
                throw new SQLException("Failed to insert customer.");
            }
        }
    }

    // Find an available table with the requested capacity or greater
    private int findAvailableTable(Connection conn, int requestedCapacity) throws SQLException {
        String availableTableSQL = "SELECT t.Table_ID " +
                "FROM Restaurant_Table t " +
                "LEFT JOIN Reservation r ON t.Table_ID = r.Table_ID " +
                "WHERE t.Capacity >= ? AND r.Table_ID IS NULL " + // Look for unreserved tables
                "ORDER BY t.Capacity ASC"; // Order by capacity to get the smallest available table

        try (PreparedStatement stmt = conn.prepareStatement(availableTableSQL)) {
            stmt.setInt(1, requestedCapacity);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Table_ID"); // Return the first available table
            } else {
                return -1; // No available table found
            }
        }
    }

    // Create a reservation for the customer with the current date and time
    private void createReservation(Connection conn, int customerId, int tableId) throws SQLException {
        String insertReservationSQL = "INSERT INTO Reservation (Customer_ID, Table_ID, Reservation_Date, Reservation_Time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertReservationSQL)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, tableId);
            stmt.setDate(3, Date.valueOf(LocalDate.now())); // Current date
            stmt.setTime(4, Time.valueOf(LocalTime.now())); // Current time
            stmt.executeUpdate();

            // After creating a reservation, create a new order
            int orderId = createNewOrder(conn, customerId);

            // Now, update the Order_Item table to set the newly created Order_ID
            updateOrderItemsWithNewOrderId(conn, orderId);
        }
    }

    private int createNewOrder(Connection conn, int customerId) throws SQLException {
        String insertOrderSQL = "INSERT INTO Orders (Customer_ID, Order_Date) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setDate(2, Date.valueOf(LocalDate.now())); // Current date
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1); // Return the generated Order_ID

                // Calculate the total amount for items with Order_ID as NULL
                double totalAmount = calculateTotalAmount(conn);

                // Optionally update the Orders table with the total amount
                String updateOrderTotalSQL = "UPDATE Orders SET Total_Amount = ? WHERE Order_ID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateOrderTotalSQL)) {
                    updateStmt.setDouble(1, totalAmount);
                    updateStmt.setInt(2, orderId);
                    updateStmt.executeUpdate();
                }

                return orderId; // Return the generated Order_ID
            } else {
                throw new SQLException("Failed to create a new order.");
            }
        }
    }

    // Method to calculate the total amount of items with Order_ID as NULL
    private double calculateTotalAmount(Connection conn) throws SQLException {
        String totalAmountSQL = "SELECT SUM(Price * Quantity) AS Total FROM Order_Item WHERE Order_ID IS NULL";

        try (PreparedStatement totalStmt = conn.prepareStatement(totalAmountSQL);
             ResultSet totalRs = totalStmt.executeQuery()) {

            if (totalRs.next()) {
                return totalRs.getDouble("Total"); // Get the total amount
            }
        }

        return 0.0; // Return 0 if no items are found
    }

    // Method to update Order_Item table with the new Order_ID
    private void updateOrderItemsWithNewOrderId(Connection conn, int orderId) throws SQLException {
        String updateOrderItemSQL = "UPDATE Order_Item SET Order_ID = ? WHERE Order_ID IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(updateOrderItemSQL)) {
            stmt.setInt(1, orderId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Updated " + rowsUpdated + " items in Order_Item with new Order_ID.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReservationUI());
    }
}
