package com.securemessaging.storage;
import com.securemessaging.Encryption.MessageEncryptor;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.sql.*;
import java.util.*;

public class MessageStore {

    private static final String DB_URL = "jdbc:sqlite:messages.db";                                                         //sqlite database location
    private final SecretKey masterKey;                                                                                      // Key used to encrypt/decrypt message

    public MessageStore(String password) throws Exception {                                                                 // Constructor taking password
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), "salt".getBytes(), 65536, 256);      // Password Based key specification and conversion password to chararray and salt.
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");                           // factory to generate secret keys and secure key
        masterKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");                           // Create AES key and generates key with the encryption algorithm
        initDB();                                                                                                          // initialize database
    }

    private void initDB() throws SQLException {                                                                            // sets up the database
        try (Connection conn = DriverManager.getConnection(DB_URL)) {                                                      // connection with database
            String sql = "CREATE TABLE IF NOT EXISTS messages (id INTEGER PRIMARY KEY, encrypted_text TEXT)";              // create table if it doesnt exist
            conn.createStatement().execute(sql);                                                                           // Execute the table
        }
    }

    public void storeMessage(String message) throws Exception {
        String encrypted = MessageEncryptor.encryptMessage(message, masterKey);                                            // encrypted version of the message
        try (Connection conn = DriverManager.getConnection(DB_URL)) {                                                      // connection with database
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages (encrypted_text) VALUES (?)");        // Sql insert into statement
            stmt.setString(1, encrypted);                                                                     // give encrypted message to query
            stmt.executeUpdate();                                                                                          // execute the insert
        }
    }

    public List<String> loadMessages() throws Exception {                                                                  // loads and decrypts messages
        List<String> messages = new ArrayList<>();                                                                         // list for stored messages
        try (Connection conn = DriverManager.getConnection(DB_URL)) {                                                      // connection with database
            ResultSet rs = conn.createStatement().executeQuery("SELECT encrypted_text FROM messages");                 // execute query to view messages
            while (rs.next()) {                                                                                            // loop to result set
                String decrypted = MessageEncryptor.decryptMessage(rs.getString("encrypted_text"), masterKey);  // decrypt every message
                messages.add(decrypted);                                                                                   // Add decrypted messages to the list (decrypted)
            }
        }
        return messages;                                                                                                   // return every decrypted message in the list
    }

}
