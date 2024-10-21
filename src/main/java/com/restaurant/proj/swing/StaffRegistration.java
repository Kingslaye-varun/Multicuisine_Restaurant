package com.restaurant.proj.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class StaffRegistration {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    // Public method to create and show the GUI
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Staff Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new GridLayout(8, 2)); // Change to 8 rows

        // Create input fields
        JTextField nameField = new JTextField();
        JTextField positionField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField managerIdField = new JTextField(); // Optional Manager ID
        JPasswordField passwordField = new JPasswordField();

        // Create labels
        frame.add(new JLabel("Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Position:"));
        frame.add(positionField);
        frame.add(new JLabel("Phone Number:"));
        frame.add(phoneField);
        frame.add(new JLabel("Email:"));
        frame.add(emailField);
        frame.add(new JLabel("Manager ID (optional):"));
        frame.add(managerIdField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);

        // Create Register button
        JButton registerButton = new JButton("Register");
        frame.add(registerButton);

        // Create Already a user button
        JButton alreadyUserButton = new JButton("Already a user?");
        frame.add(alreadyUserButton);

        // Register button action listener
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerStaff(nameField.getText(), positionField.getText(), phoneField.getText(),
                        emailField.getText(), managerIdField.getText(), new String(passwordField.getPassword()));
            }
        });

        // Already a user button action listener
        alreadyUserButton.addActionListener(e -> {
            new StaffLogin().createAndShowGUI(); // Redirect to Staff Login
            frame.dispose(); // Close the registration frame
        });

        // Set frame visibility and center it
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void registerStaff(String name, String position, String phone, String email, String managerId, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Hash the password using bcrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Prepare SQL statement
            String sql = "INSERT INTO Staff (Name, Position, Phone_Number, Email, Manager_ID, Password) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, position);
                pstmt.setString(3, phone);
                pstmt.setString(4, email);
                pstmt.setObject(5, managerId.isEmpty() ? null : Integer.parseInt(managerId)); // Set to null if empty
                pstmt.setString(6, hashedPassword);

                // Execute the insert operation
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Staff registered successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to register staff: " + e.getMessage());
        }
    }
}
