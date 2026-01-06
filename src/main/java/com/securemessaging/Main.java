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
    private static final int DEFAULT_PORT = 12345;                                            //Default network port
    private static final String DEFAULT_HOST = "localhost";                                   //Default server host

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);                                             //Scanner for console input

        System.out.println("Run as (S)erver or (C)lient?");                                   //user chooses mode
        String mode = scanner.nextLine().toUpperCase();                                       //Read and normalize input

        int port = DEFAULT_PORT;                                                              //initialize port with default
        String host = DEFAULT_HOST;                                                           //initialize host with default

        if (mode.equals("C")){                                                                //If running as (C)lient
            System.out.println("Enter host (default: localhost): ");                          //Enter server address
            String inputHost = scanner.nextLine();                                            //Read host input
            if (!inputHost.isEmpty()) host = inputHost;                                       //Override default if given
        }

        System.out.println("Enter port (default: 12345): ");                                  //Enter port number
        String inputPort = scanner.nextLine();                                                //Read port input
        if (!inputPort.isEmpty())                                                             //If user entered a port
            port = Integer.parseInt(inputPort);                                               //change string to int

        System.out.println("Enter master password for message storage: ");                    //Enter encryption password
        String password = scanner.nextLine();                                                 //Read password

        try {
            SessionManager sessionManager = new SessionManager();                             //handles secure key exchange
            SocketCommunicator communicator = new SocketCommunicator();                       //Manage socket communication
            Obfuscator obfuscator = new Obfuscator();                                         //Generates dummy traffic
            MessageStore messageStore = new MessageStore(password);                           //Encrypts stored messages
            ChatGUI gui = new ChatGUI();                                                      //the chat GUI

            javax.swing.SwingUtilities.invokeLater(() -> gui.setVisible(true));               //run gui and display chat window

            if (mode.equals("S")) {                                                           //If running as (S)erver
                communicator.startServer(port);                                               //start listening for clients
                System.out.println("Server started. Waiting for connection...");              //print that server has started
            } else {                                                                          //else client mode
                communicator.connectToServer(host, port);                                     //Connect to the server
                System.out.println("Connected to server.");                                   //print connected to server
            }

            sessionManager.performKeyExchange(communicator);                                  //Key exchange
            sessionManager.startKeyRefresh(communicator);                                     //Periodically refresh the key
            obfuscator.startDummyTraffic(communicator);                                       //hide real traffic with dummy traffic

            ExecutorService executor = Executors.newCachedThreadPool();                       //Thread pool for async tasks, prevents leaks

            executor.submit(() -> {                                                           //background threat for receiving messages
                while (true) {                                                                //Keep listening continuously
                    try {
                        String receivedPacket = communicator.receivePacket();                 //Read incoming data
                        if (receivedPacket.startsWith("DUMMY:")) continue;                    //ignore dummy traffic
                        String decrypted = MessageEncryptor.decryptMessage(receivedPacket, sessionManager.getSharedAESKey()); //decrypt the real message
                        messageStore.storeMessage("Peer: " + decrypted);                      //Store the message encrypted
                        gui.receiveMessage(decrypted);                                        //the gui display the message
                    } catch (Exception e) {                                                   //Handle error for network/crypto
                        e.printStackTrace();                                                  //Print error for debugging
                        break;                                                                //exit loop
                    }
                }
            });

            System.out.println("Type messages to send (type 'exit' to quit):");               //instructions for exiting
            while (true) {                                                                    //Loop for sending messages
                String message = scanner.nextLine();                                          //Read user's messages
                if ("exit".equalsIgnoreCase(message)) break;                                  //if user type exit to quit

                String encrypted = MessageEncryptor.encryptMessage(message, sessionManager.getSharedAESKey());  //Encrypt message before sending
                messageStore.storeMessage("You: " + message);                                 //Store message
                obfuscator.addTimingJitter(() -> {                                            //Add random delay for security
                    try {
                        communicator.sendPacket(encrypted);                                   //Send encrypted message
                    } catch (Exception e) {                                                   //Handle send errors
                        e.printStackTrace();                                                  //print error
                    }
                });
            }

            executor.shutdown();                                                              //Stop background threads
            obfuscator.shutdown();                                                            //Stop dummy traffic
            sessionManager.shutdown();                                                        //Clean up crypto resource
            communicator.close();                                                             //close connection

        } catch (Exception e) {                                                               //catch unexpected errors
            e.printStackTrace();                                                              //Print error details
        } finally {
            scanner.close();                                                                  //Release input
        }
    }

    }
