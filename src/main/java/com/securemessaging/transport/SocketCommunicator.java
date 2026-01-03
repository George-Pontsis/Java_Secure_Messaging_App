package com.securemessaging.transport;
import java.io.*;
import java.net.*;

public class SocketCommunicator {
        private static final int PACKET_SIZE = 1024;
        private static final String PADDING_CHAR = " ";

        private ServerSocket serverSocket;
        private Socket clientSocket;
        private DataInputStream in;
        private DataOutputStream out;

        public void startServer(int port) throws IOException {
            serverSocket = new ServerSocket(port);
            System.out.println("Secure Messaging App server listening on port " + port);
            clientSocket = serverSocket.accept();
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        }

        public void connectToServer(String host, int port) throws IOException {
            clientSocket = new Socket(host, port);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        }

        public void sendPacket(String data) throws IOException {
            String paddedData = padData(data);
            out.writeUTF(paddedData);
            out.flush();
        }

        public String receivePacket() throws IOException {
            String paddedData = in.readUTF();
            return unpadData(paddedData);
        }

        private String padData(String data) {
            if (data.length() >= PACKET_SIZE) return data.substring(0, PACKET_SIZE);
            return data + PADDING_CHAR.repeat(PACKET_SIZE - data.length());
        }

        private String unpadData(String data) {
            return data.trim();
        }

        public void close() throws IOException {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        }
}
