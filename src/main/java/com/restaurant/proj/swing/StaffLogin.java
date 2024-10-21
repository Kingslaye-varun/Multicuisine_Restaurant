package com.restaurant.proj.swing;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StaffLogin {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffLogin().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Staff Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(4, 2));

        // Create input fields
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        // Create labels
        frame.add(new JLabel("Email:"));
        frame.add(emailField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);

        // Create Login button
        JButton loginButton = new JButton("Login");
        frame.add(loginButton);

        // Create Signup button
        JButton signupButton = new JButton("Signup");
        frame.add(signupButton);

        // Login button action listener
        loginButton.addActionListener(e -> {
            // Call method to perform login and pass the frame reference
            loginStaff(emailField.getText(), new String(passwordField.getPassword()), frame);
        });

        // Signup button action listener
        signupButton.addActionListener(e -> {
            new StaffRegistration().createAndShowGUI(); // Redirect to Staff Registration
            frame.dispose(); // Close the login frame
        });

        // Set frame visibility and center it
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loginStaff(String email, String password, JFrame loginFrame) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Prepare SQL statement
            String sql = "SELECT Password FROM Staff WHERE Email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String hashedPassword = rs.getString("Password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        JOptionPane.showMessageDialog(null, "Login successful!");

                        
                        // Close the login window
                        loginFrame.dispose(); // Dispose of the login frame
                        // Open MainApp after successful login
                        new MainApp();


                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid email or password.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Login failed: " + e.getMessage());
        }
    }
}
