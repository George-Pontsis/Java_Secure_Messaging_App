package com.securemessaging.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JTextArea logArea;

    public ChatGUI() {
        setTitle("Secure Messaging App");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        logArea = new JTextArea(5, 20);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.EAST);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            chatArea.append("You: " + message + "\n");
            inputField.setText("");
            logMetadata("Sent message at " + System.currentTimeMillis());
        }
    }

    public void receiveMessage(String message) {
        chatArea.append("Peer: " + message + "\n");
        logMetadata("Received message at " + System.currentTimeMillis());
    }

    private void logMetadata(String log) {
        logArea.append(log + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI().setVisible(true));
    }
}
