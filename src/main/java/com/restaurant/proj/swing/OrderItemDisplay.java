package com.restaurant.proj.swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OrderItemDisplay extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection connection;

    public OrderItemDisplay() {
        // Set up the frame
        setTitle("Order Items");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set up the table with Menu_Item_ID displayed but hidden
        model = new DefaultTableModel(new String[]{"Menu_Item_ID", "Item Name", "Price", "Quantity", "Delete"}, 0);
        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the "Delete" button column is editable
            }

            // Override to display the JButton in the "Delete" column
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 4) {
                    return new ButtonRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };

        // Hide the first column (Menu_Item_ID)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Set custom editor for the "Delete" button
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Wrap the table in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Load data from the database
        loadOrderItems();

        // Add Reserve Table button
        JButton reserveButton = new JButton("Reserve Table");
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ReservationUI(); // Create and show Reservation UI
            }
        });

        // Add the button to the bottom of the frame
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(reserveButton);
        add(buttonPanel, BorderLayout.SOUTH); // Add the button panel to the bottom

        setVisible(true);
    }

    private void loadOrderItems() {
        try {
            // Establish database connection
            System.out.println("Connecting to database...");
            String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
            connection = DriverManager.getConnection(connectionUrl, "sa", "1234");
            System.out.println("Connection successful.");

            // SQL query to retrieve order items and menu item details where Order_ID is NULL
            String sql = "SELECT oi.Menu_Item_ID, mi.Name, oi.Price, oi.Quantity " +
                         "FROM Order_Item oi " +
                         "JOIN Menu_Item mi ON oi.Menu_Item_ID = mi.Menu_Item_ID " +
                         "WHERE oi.Order_ID IS NULL"; // Filter for order items with NULL Order_ID
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("Query executed, populating table...");

            // Clear existing rows in the table model
            model.setRowCount(0);

            // Populate the table with data
            while (resultSet.next()) {
                int menuItemId = resultSet.getInt("Menu_Item_ID");
                String itemName = resultSet.getString("Name");
                double price = resultSet.getDouble("Price");
                int quantity = resultSet.getInt("Quantity");

                System.out.println("Retrieved: " + itemName + " | Price: " + price + " | Quantity: " + quantity);
                
                // Add row to table (Menu_Item_ID is hidden from the user)
                model.addRow(new Object[]{menuItemId, itemName, price, quantity, "Delete"});
            }

            // After loading data, debug print the Menu_Item_IDs
            System.out.print("Current Menu_Item_IDs in table: ");
            for (int i = 0; i < model.getRowCount(); i++) {
                System.out.print(model.getValueAt(i, 0) + " ");
            }
            System.out.println();

            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("Data loaded successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading data from database: " + e.getMessage());
        }
    }

    private void deleteOrderItem(int menuItemId) {
        try {
            System.out.println("Deleting item with Menu_Item_ID: " + menuItemId);

            // Establish database connection
            String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Restaurant;encrypt=true;trustServerCertificate=true";
            connection = DriverManager.getConnection(connectionUrl, "sa", "1234");

            String sql = "DELETE FROM Order_Item WHERE Menu_Item_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, menuItemId);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
            System.out.println("Item deleted successfully.");

            // Reload the data to refresh the table
            SwingUtilities.invokeLater(() -> {
                model.setRowCount(0); // Clear the table
                loadOrderItems(); // Reload the data
            });

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting item from database: " + e.getMessage());
        }
    }

    // Renderer for the "Delete" button
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Delete");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor for the "Delete" button
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int menuItemId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isPushed) {
                        // Get the menuItemId from the table model
                        int row = table.getSelectedRow();
                        if (row != -1) {
                            menuItemId = (int) table.getModel().getValueAt(row, 0);
                            deleteOrderItem(menuItemId); // Call delete method
                        }
                    }
                    fireEditingStopped(); // Stop editing to close the editor
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Delete" : value.toString();
            button.setText(label);
            isPushed = true;

            // Get the menuItemId from the table model
            menuItemId = (int) table.getModel().getValueAt(row, 0); 

            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderItemDisplay());
    }
}
