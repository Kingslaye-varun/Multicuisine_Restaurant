package com.restaurant.proj.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuUI {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "1234";

    private static JPanel startersPanel;
    private static JPanel mainCoursePanel;
    private static JPanel dessertPanel;

    
    // Constructor to initialize the UI
    public MenuUI() {
        createAndShowGUI(); // Call to setup GUI
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Restaurant Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600); // Increase size for navbar

        // Create the navbar
        JPanel navbar = createNavbar(frame);
        frame.add(navbar, BorderLayout.NORTH);

        // Main panel for menu items
        JPanel panel = new JPanel(new GridLayout(3, 1));

        // Panels for each category
        startersPanel = createCategoryPanel("Starters");
        mainCoursePanel = createCategoryPanel("Main Course");
        dessertPanel = createCategoryPanel("Dessert");

        // Add the panels to the main panel
        panel.add(startersPanel);
        panel.add(mainCoursePanel);
        panel.add(dessertPanel);

        frame.add(new JScrollPane(panel), BorderLayout.CENTER); // Add scroll if needed
        frame.setVisible(true);
    }

    

    private static JPanel createNavbar(JFrame frame) {
        JPanel navbar = new JPanel(new BorderLayout());

        // Restaurant name on the left
        JLabel restaurantLabel = new JLabel("Restaurant");
        navbar.add(restaurantLabel, BorderLayout.WEST);

        // Center panel for search box and button
        JPanel centerPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        
        // Action listener for search button
        searchButton.addActionListener(e -> searchMenuItems(searchField.getText()));
        
        centerPanel.add(searchField);
        centerPanel.add(searchButton);
        
        navbar.add(centerPanel, BorderLayout.CENTER); // Center the search box and button

        // Cart button on the extreme right
        JButton cartButton = new JButton("Cart");
        cartButton.addActionListener(e -> {
            // Launch the OrderItemDisplay app in a new JFrame
            SwingUtilities.invokeLater(() -> {
                OrderItemDisplay orderDisplay = new OrderItemDisplay();
                orderDisplay.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this frame
                orderDisplay.setVisible(true);
            });
        });
        navbar.add(cartButton, BorderLayout.EAST); // Add cart button to the right

        return navbar;
    }

    private static void searchMenuItems(String searchTerm) {
        // Clear existing items from the panels
        startersPanel.removeAll();
        mainCoursePanel.removeAll();
        dessertPanel.removeAll();

        // Fetch menu items based on the search term
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT Menu_Item_ID, Name, Price, Category FROM Menu_Item WHERE Name LIKE ?")) {

            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            boolean itemsFound = false; // Flag to check if any items were found

            while (rs.next()) {
                int menuItemId = rs.getInt("Menu_Item_ID");  // Fetch the Menu_Item_ID
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                String category = rs.getString("Category");

                // Create a new menu item with the correct ID
                MenuItem item = new MenuItem(name, price, menuItemId); // Pass the Menu_Item_ID here

                // Add the item to the appropriate panel
                if ("Starters".equalsIgnoreCase(category)) {
                    addMenuItemToPanel(startersPanel, item);
                    itemsFound = true; // Mark that we found at least one item
                } else if ("Main Course".equalsIgnoreCase(category)) {
                    addMenuItemToPanel(mainCoursePanel, item);
                    itemsFound = true;
                } else if ("Dessert".equalsIgnoreCase(category)) {
                    addMenuItemToPanel(dessertPanel, item);
                    itemsFound = true;
                }
            }

            // If no items found, display a message
            if (!itemsFound) {
                JLabel noItemsLabel = new JLabel("No items found");
                startersPanel.add(noItemsLabel);
                mainCoursePanel.add(noItemsLabel);
                dessertPanel.add(noItemsLabel);
            }

            // Refresh the panels
            startersPanel.revalidate();
            mainCoursePanel.revalidate();
            dessertPanel.revalidate();
            startersPanel.repaint();
            mainCoursePanel.repaint();
            dessertPanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error searching menu items: " + e.getMessage());
        }
    }


    private static void addMenuItemToPanel(JPanel categoryPanel, MenuItem item) {
        // Create components for the menu item
        JLabel nameLabel = new JLabel(item.getName());
        JLabel priceLabel = new JLabel(String.valueOf(item.getPrice()));
        JButton orderButton = new JButton("Order");

        // Add action listener to the button
        orderButton.addActionListener(e -> {
            // Call method to process the order
            addOrderItem(item);
        });

        // Add components to the category panel
        categoryPanel.add(nameLabel);    // First column for the name
        categoryPanel.add(priceLabel);    // Second column for the price
        categoryPanel.add(orderButton);   // Third column for the button
    }

    private static JPanel createCategoryPanel(String category) {
        JPanel categoryPanel = new JPanel(new GridLayout(0, 3)); // 0 rows for dynamic growth, 3 columns
        categoryPanel.setBorder(BorderFactory.createTitledBorder(category)); // Category title

        // Fetch data from the database for each category
        List<MenuItem> menuItems = fetchMenuItemsByCategory(category);

        // Display the items with buttons
        for (MenuItem item : menuItems) {
            addMenuItemToPanel(categoryPanel, item);
        }

        return categoryPanel;
    }

    private static List<MenuItem> fetchMenuItemsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT Menu_Item_ID, Name, Price FROM Menu_Item WHERE Category = ?")) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int menuItemId = rs.getInt("Menu_Item_ID"); // Fetch ID
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                items.add(new MenuItem(name, price, menuItemId)); // Include ID in MenuItem
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private static void addOrderItem(MenuItem item) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println(item.getMenuItemId());
            // Check if the item already exists in Order_Item table
            String checkItemSQL = "SELECT Quantity FROM Order_Item WHERE Order_ID = NULL and Menu_Item_ID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkItemSQL)) {
                checkStmt.setInt(1, item.getMenuItemId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Item exists, update the quantity
                    int currentQuantity = rs.getInt("Quantity");
                    String updateQuantitySQL = "UPDATE Order_Item SET Quantity = ? WHERE Menu_Item_ID = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuantitySQL)) {
                        updateStmt.setInt(1, currentQuantity + 1);
                        updateStmt.setInt(2, item.getMenuItemId());
                        updateStmt.executeUpdate();
                    }
                    JOptionPane.showMessageDialog(null, "Item quantity updated successfully!");
                } else {
                    // Item doesn't exist, insert it with quantity = 1
                    String insertOrderItemSQL = "INSERT INTO Order_Item (Order_ID, Menu_Item_ID, Quantity, Price) VALUES (NULL, ?, ?, ?)";
                    try (PreparedStatement orderItemStmt = conn.prepareStatement(insertOrderItemSQL)) {
                        orderItemStmt.setInt(1, item.getMenuItemId()); // Menu_Item_ID
                        orderItemStmt.setInt(2, 1); // Quantity set to 1 for this new order
                        orderItemStmt.setDouble(3, item.getPrice());
                        orderItemStmt.executeUpdate();
                    }
                    JOptionPane.showMessageDialog(null, "Item ordered successfully!");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to place order: " + e.getMessage());
        }
    }
}

class MenuItem {
    private String name;
    private double price;
    private int menuItemId; // Add ID field

    public MenuItem(String name, double price, int menuItemId) {
        this.name = name;
        this.price = price;
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getMenuItemId() {
        return menuItemId; // Getter for the ID
    }
}
