package com.securemessaging.transport;
import java.io.*;
import java.net.*;

public class SocketCommunicator {                                                                            //Handles sending and receiving messages over sockets
        private static final int PACKET_SIZE = 1024;                                                         //Fixed packet size to reduce traffic analysis
        private static final String PADDING_CHAR = " ";                                                      //Character used to pad messages

        private ServerSocket serverSocket;                                                                   //Listens for incoming client connections
        private Socket clientSocket;                                                                         //Represents the active network connection
        private DataInputStream in;                                                                          //Reads incoming data
        private DataOutputStream out;                                                                        //Reads outgoing data

        public void startServer(int port) throws IOException {                                               //Starts the app in server mode
            serverSocket = new ServerSocket(port);                                                           //Open server socket on given port
            System.out.println("Secure Messaging App server listening on port " + port);                     //Display server status
            clientSocket = serverSocket.accept();                                                            //Wait and accept client connection
            in = new DataInputStream(clientSocket.getInputStream());                                         //initialize input stream
            out = new DataOutputStream(clientSocket.getOutputStream());                                      //Initialize output stream
        }

        public void connectToServer(String host, int port) throws IOException {                              //Connects to the server in client mode
            clientSocket = new Socket(host, port);                                                           //Creates connection to the server
            in = new DataInputStream(clientSocket.getInputStream());                                         //Initialize input stream
            out = new DataOutputStream(clientSocket.getOutputStream());                                      //Initialize output stream
        }

        public void sendPacket(String data) throws IOException {                                             //Sends a message packet
            String paddedData = padData(data);                                                               //Pad data to fixed size
            out.writeUTF(paddedData);                                                                        //Send padded message
            out.flush();                                                                                     //Flush the data out
        }

        public String receivePacket() throws IOException {                                                   //Receives a message packet
            String paddedData = in.readUTF();                                                                //Read padded data from socket
            return unpadData(paddedData);                                                                    //Remove padding and return message
        }

        private String padData(String data) {                                                                //Adds padding for traffic obfuscation
            if (data.length() >= PACKET_SIZE) return data.substring(0, PACKET_SIZE);                         //If data is too large return cut data to fix the packet
            return data + PADDING_CHAR.repeat(PACKET_SIZE - data.length());                            //Add padding characters
        }

        private String unpadData(String data) {                                                              //Removes padding after receiving
            return data.trim();                                                                              //Remove extra spaces
        }

        public void close() throws IOException {                                                             //Closes all network resources
            if (in != null) in.close();                                                                      //Close input stream
            if (out != null) out.close();                                                                    //Close output stream
            if (clientSocket != null) clientSocket.close();                                                  //Close client socket
            if (serverSocket != null) serverSocket.close();                                                  //Close server socket
        }
}
