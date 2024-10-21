package com.restaurant.proj.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.*;

public class AddMenuItemApp {

    // Components for the form
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryDropdown;
    private JButton addButton;

    // Database connection details
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    public AddMenuItemApp() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Add New Menu Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Keep the main app open
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(4, 2));

        // Form components
        JLabel nameLabel = new JLabel("Item Name:");
        nameField = new JTextField();

        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:");
        categoryDropdown = new JComboBox<>(new String[]{"Starters", "Main Course", "Dessert"});

        addButton = new JButton("Add Item");

        // Add components to the frame
        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(priceLabel);
        frame.add(priceField);
        frame.add(categoryLabel);
        frame.add(categoryDropdown);
        frame.add(new JLabel());  // Empty space for layout
        frame.add(addButton);

        // Add action listener for the Add button
        addButton.addActionListener(this::addMenuItem);

        frame.setVisible(true); // Display the frame
    }

    private void addMenuItem(ActionEvent e) {
        String name = nameField.getText();
        String priceText = priceField.getText();
        String category = (String) categoryDropdown.getSelectedItem();

        if (validateInput(name, priceText)) {
            addMenuItemToDatabase(name, priceText, category);
        } else {
            JOptionPane.showMessageDialog(null, "Please enter valid data.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Validate the input from the form
    private boolean validateInput(String name, String priceText) {
        try {
            if (name.trim().isEmpty() || priceText.trim().isEmpty()) {
                return false;
            }
            Double.parseDouble(priceText);  // Ensure price is a valid number
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method to add the menu item to the database
    private void addMenuItemToDatabase(String name, String priceText, String category) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Menu_Item (Name, Price, Category) VALUES (?, ?, ?)")) {

            statement.setString(1, name);
            statement.setBigDecimal(2, new BigDecimal(priceText));
            statement.setString(3, category);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clear the form after successfully adding an item
    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        categoryDropdown.setSelectedIndex(0);  // Reset dropdown to the first option
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddMenuItemApp::new);
    }
}
