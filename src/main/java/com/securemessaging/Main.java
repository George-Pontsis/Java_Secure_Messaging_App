package com.securemessaging;
import com.securemessaging.Encryption.KeyManager;
import com.securemessaging.Encryption.MessageEncryptor;
import com.securemessaging.gui.ChatGUI;
import com.securemessaging.session.SessionManager;
import com.securemessaging.obfuscation.Obfuscator;
import com.securemessaging.storage.MessageStore;
import com.securemessaging.transport.SocketCommunicator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    private static final int DEFAULT_PORT = 12345;
    private static final String DEFAULT_HOST = "localhost";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Run as (S)erver or (C)lient?");
        String mode = scanner.nextLine().toUpperCase();

        int port = DEFAULT_PORT;
        String host = DEFAULT_HOST;
        if (mode.equals("C")){
            System.out.println("Enter host (default: localhost): ");
            String inputHost = scanner.nextLine();
            if (!inputHost.isEmpty()) host = inputHost;
        }
        System.out.println("Enter port (default: 12345): ");
        String inputPort = scanner.nextLine();
        if (!inputPort.isEmpty()) port = Integer.parseInt(inputPort);

        System.out.println("Enter master password for message storage: ");
        String password = scanner.nextLine();

        try {
            SessionManager sessionManager = new SessionManager();
            SocketCommunicator communicator = new SocketCommunicator();
            Obfuscator obfuscator = new Obfuscator();
            MessageStore messageStore = new MessageStore(password);
            ChatGUI gui = new ChatGUI();

            javax.swing.SwingUtilities.invokeLater(() -> gui.setVisible(true));

            if (mode.equals("S")) {
                communicator.startServer(port);
                System.out.println("Server started. Waiting for connection...");
            } else {
                communicator.connectToServer(host, port);
                System.out.println("Connected to server.");
            }

            sessionManager.performKeyExchange(communicator);
            sessionManager.startKeyRefresh(communicator);
            obfuscator.startDummyTraffic(communicator);

            ExecutorService executor = Executors.newCachedThreadPool();

            executor.submit(() -> {
                while (true) {
                    try {
                        String receivedPacket = communicator.receivePacket();
                        if (receivedPacket.startsWith("DUMMY:")) continue;
                        String decrypted = MessageEncryptor.decryptMessage(receivedPacket, sessionManager.getSharedAESKey());
                        messageStore.storeMessage("Peer: " + decrypted);
                        gui.receiveMessage(decrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            });

            System.out.println("Type messages to send (type 'exit' to quit):");
            while (true) {
                String message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) break;

                String encrypted = MessageEncryptor.encryptMessage(message, sessionManager.getSharedAESKey());
                messageStore.storeMessage("You: " + message);
                obfuscator.addTimingJitter(() -> {
                    try {
                        communicator.sendPacket(encrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();
            obfuscator.shutdown();
            sessionManager.shutdown();
            communicator.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    }
