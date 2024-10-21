/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.proj.swing;

/**
 *
 * @author Nidhi
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CustomerSearchApp extends JFrame {

    // Components
    private JTextField searchField;
    private JButton searchButton;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    // Database connection details
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    public CustomerSearchApp() {
        setTitle("Customer Search");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with search field and button
        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        // Table model for customer data
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] { "Customer ID", "Name", "Phone Number", "Email" });

        // Customer table
        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listener to search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                searchCustomers(searchText);
            }
        });

        // Load all customers initially when the app starts
        searchCustomers(""); 
    }

    // Method to search customers from database
    private void searchCustomers(String searchText) {
        // Clear current table data
        tableModel.setRowCount(0);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            String query;
            if (searchText.trim().isEmpty()) {
                // If the search text is empty, select all customers
                query = "SELECT * FROM Customer";
                statement = connection.prepareStatement(query);
            } else {
                // Otherwise, search customers by name, phone, or email
                query = "SELECT * FROM Customer WHERE Name LIKE ? OR Phone_Number LIKE ? OR Email LIKE ?";
                statement = connection.prepareStatement(query);
                String searchPattern = "%" + searchText + "%";
                statement.setString(1, searchPattern);
                statement.setString(2, searchPattern);
                statement.setString(3, searchPattern);
            }

            resultSet = statement.executeQuery();

            // Add results to table
            while (resultSet.next()) {
                int customerId = resultSet.getInt("Customer_ID");
                String name = resultSet.getString("Name");
                String phone = resultSet.getString("Phone_Number");
                String email = resultSet.getString("Email");

                tableModel.addRow(new Object[] { customerId, name, phone, email });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close database resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CustomerSearchApp().setVisible(true);
            }
        });
    }
}
