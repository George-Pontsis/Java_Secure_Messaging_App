package com.securemessaging.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;                                            // Area showing all chat messages
    private JTextField inputField;                                         // Field where user types messages
    private JButton sendButton;                                            // Button to send message
    private JTextArea logArea;                                             // Area showing metadata/logs

    public ChatGUI() {
        setTitle("Secure Messaging App");                                  // Window title
        setSize(600, 400);                                    // Window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                   // Close program on exit
        setLayout(new BorderLayout());                                    // Layout manager

        chatArea = new JTextArea();                                       // Initialize chat area
        chatArea.setEditable(false);                                      // Make it read-only
        add(new JScrollPane(chatArea), BorderLayout.CENTER);              // center chat with scroll

        JPanel bottomPanel = new JPanel(new BorderLayout());              // Panel for input + button
        inputField = new JTextField();                                    // text input field
        sendButton = new JButton("Send");                            // send button
        bottomPanel.add(inputField, BorderLayout.CENTER);                 // input in centre
        bottomPanel.add(sendButton, BorderLayout.EAST);                   // button on right
        add(bottomPanel, BorderLayout.SOUTH);                             // panel at bottom

        logArea = new JTextArea(5, 20);                      // Metadata log area
        logArea.setEditable(false);                                       // Read-only logs
        add(new JScrollPane(logArea), BorderLayout.EAST);                 // Log area on the right

        sendButton.addActionListener(e -> sendMessage());      // Send on button click
        inputField.addActionListener(e -> sendMessage());      // Send on Enter key
    }

    private void sendMessage() {
        String message = inputField.getText();                             // read user input
        if (!message.isEmpty()) {                                          // ignore empty messages
            chatArea.append("You: " + message + "\n");                     // show sent message
            inputField.setText("");                                        // clear input field
            logMetadata("Sent message at " + System.currentTimeMillis());  // Log timing
        }
    }

    public void receiveMessage(String message) {
        chatArea.append("Peer: " + message + "\n");                        // Display received message
        logMetadata("Received message at " + System.currentTimeMillis());  // Log timing
    }

    private void logMetadata(String log) {
        logArea.append(log + "\n");                                        // Append metadata to log area
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI().setVisible(true));  // Start GUI

    }
}
