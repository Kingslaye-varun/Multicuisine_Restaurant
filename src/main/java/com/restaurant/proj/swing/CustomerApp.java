/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.proj.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomerApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Customer Registration");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 20, 165, 25);
        panel.add(nameText);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(10, 50, 80, 25);
        panel.add(phoneLabel);

        JTextField phoneText = new JTextField(20);
        phoneText.setBounds(100, 50, 165, 25);
        panel.add(phoneText);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 80, 80, 25);
        panel.add(emailLabel);

        JTextField emailText = new JTextField(20);
        emailText.setBounds(100, 80, 165, 25);
        panel.add(emailText);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(10, 120, 150, 25);
        panel.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameText.getText();
                String phone = phoneText.getText();
                String email = emailText.getText();

                // Sending data to the backend
                try {
                    URL url = new URL("http://localhost:8080/customers/add");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");  // Change to JSON
                    conn.setRequestProperty("Accept", "application/json");

                    // Create a JSON string
                    String jsonInputString = "{ \"name\": \"" + name + "\", \"phoneNumber\": \"" + phone + "\", \"email\": \"" + email + "\" }";

                    // Send JSON input
                    OutputStream os = conn.getOutputStream();
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    os.close();

                    // Check the response code
                    int responseCode = conn.getResponseCode();
                    System.out.println("Response Code: " + responseCode); // Debug line
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        JOptionPane.showMessageDialog(null, "Customer added successfully!");
                    } else {
                        // Read error stream
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String inputLine;
                        StringBuilder errorResponse = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            errorResponse.append(inputLine);
                        }
                        in.close();

                        // Print the error response for debugging
                        System.out.println("Error Response: " + errorResponse.toString()); // Debug line
                        JOptionPane.showMessageDialog(null, "Failed to add customer: " + errorResponse.toString());
                    }
                    conn.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
