package com.securemessaging.storage;
import com.securemessaging.Encryption.MessageEncryptor;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.sql.*;
import java.util.*;

public class MessageStore {

    private static final String DB_URL = "jdbc:sqlite:messages.db";
    private final SecretKey masterKey;

    public MessageStore(String password) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), "salt".getBytes(), 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        masterKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        initDB();
    }

    private void initDB() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS messages (id INTEGER PRIMARY KEY, encrypted_text TEXT)";
            conn.createStatement().execute(sql);
        }
    }

    public void storeMessage(String message) throws Exception {
        String encrypted = MessageEncryptor.encryptMessage(message, masterKey);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages (encrypted_text) VALUES (?)");
            stmt.setString(1, encrypted);
            stmt.executeUpdate();
        }
    }

    public List<String> loadMessages() throws Exception {
        List<String> messages = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT encrypted_text FROM messages");
            while (rs.next()) {
                String decrypted = MessageEncryptor.decryptMessage(rs.getString("encrypted_text"), masterKey);
                messages.add(decrypted);
            }
        }
        return messages;
    }

}
