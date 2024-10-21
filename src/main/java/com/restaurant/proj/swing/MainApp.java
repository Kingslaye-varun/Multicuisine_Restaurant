/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.proj.swing;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainApp {

    public MainApp() {
        createAndShowGUI();
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Restaurant Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Set padding

        // Button for Today's Menu
        JButton menuButton = new JButton("Today's Menu");
        menuButton.addActionListener(this::openAddMenuItemApp);

        // Button for Orders
        JButton ordersButton = new JButton("Orders");
        ordersButton.addActionListener(this::openOrderManagementUI);

        // Button for Tables
        JButton tablesButton = new JButton("Tables");
        tablesButton.addActionListener(this::openTableManagementUI);

        // Add buttons to the frame
        frame.add(menuButton);
        frame.add(ordersButton);
        frame.add(tablesButton);

        frame.setVisible(true);
    }

    // Action to redirect to AddMenuItemApp
    private void openAddMenuItemApp(ActionEvent e) {
        // Instantiate and show AddMenuItemApp
        new AddMenuItemApp(); // Assuming you have a constructor in this class
    }

    // Action to redirect to OrderManagementUI
    private void openOrderManagementUI(ActionEvent e) {
        // Instantiate and show OrderManagementUI
        new OrderManagementUI(); // Assuming you have a constructor in this class
    }

    // Action to redirect to TableManagementUI
    private void openTableManagementUI(ActionEvent e) {
        // Instantiate and show TableManagementUI
        new TableManagementUI(); // Assuming you have a constructor in this class
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
