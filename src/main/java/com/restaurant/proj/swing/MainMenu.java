package com.restaurant.proj.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Restaurant Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(3, 1)); // 3 rows for the title and buttons

        // Title label
        JLabel titleLabel = new JLabel("Welcome to the Restaurant Management System", SwingConstants.CENTER);
        frame.add(titleLabel);

        // Customer button
        JButton customerButton = new JButton("Customer");
        customerButton.addActionListener(e -> {
            // Redirect to MenuUI
            new com.restaurant.proj.swing.MenuUI(); // Ensure the correct package is used for MenuUI
            frame.dispose(); // Close the main menu frame
        });
        frame.add(customerButton);

        // Staff button
        JButton staffButton = new JButton("Staff");
        staffButton.addActionListener(e -> {
            // Redirect to Staff Login
            new StaffLogin().createAndShowGUI(); // Assuming StaffLogin has a public method to show the GUI
            frame.dispose(); // Close the main menu frame
        });
        frame.add(staffButton);

        frame.setVisible(true);
    }
}
