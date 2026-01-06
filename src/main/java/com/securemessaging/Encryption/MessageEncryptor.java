package com.securemessaging.Encryption;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;

public class MessageEncryptor {                                                                             //Class that handles message encryption and integrity

    private static final String AES_ALGO = "AES/GCM/NoPadding";                                             //AES mode with integrity protection
    private static final int GCM_TAG_LENGTH = 128;                                                          //Authentication tag size (in bits)

    public static String encryptMessage(String message, SecretKey aesKey) throws Exception {                //Encrypts a plain text message
        Cipher cipher = Cipher.getInstance(AES_ALGO);                                                       //Create AES-GCM cipher
        byte[] iv = new byte[12];                                                                           //Create random Initialization Vector
        new SecureRandom().nextBytes(iv);                                                                   //Fill IV with random bytes
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);                                   //Set GCM parameters (IV + TAG length)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);                                                     //Initialize cipher for encryption
        byte[] encrypted = cipher.doFinal(message.getBytes());                                              //Encrypt message bytes
        byte[] result = new byte[iv.length + encrypted.length];                                             //Free space for IV + ciphertext
        System.arraycopy(iv, 0, result, 0, iv.length);                                       //Copy IV to the beginning of the result
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);                         //append encrypted message after IV
        return Base64.getEncoder().encodeToString(result);                                                  //Encode result as safe string
    }

    public static String decryptMessage(String encryptedMessage, SecretKey aesKey) throws Exception {       //Decrypts an encrypted message
        byte[] data = Base64.getDecoder().decode(encryptedMessage);                                         //Decode Base64 back to bytes
        byte[] iv = new byte[12];                                                                           //Prepare IV array
        System.arraycopy(data, 0, iv, 0, iv.length);                                         //Extract IV from the start of data
        byte[] encrypted = new byte[data.length - iv.length];                                               //Prepare array for ciphertext
        System.arraycopy(data, iv.length, encrypted, 0, encrypted.length);                          //Extract encrypted message bytes
        Cipher cipher = Cipher.getInstance(AES_ALGO);                                                       //Create AES cipher
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);                                   //Reuse IV and TAG settings
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);                                                     //Initialize cipher for decryption
        return new String(cipher.doFinal(encrypted));                                                       //Decrypt and return plain text
    }

    public static String generateHMAC(String message, SecretKey hmacKey) throws Exception {                 //Generates HMAC for integrity check
        Mac mac = Mac.getInstance("HmacSHA256");                                                   //Create HMAC using SHA-256
        mac.init(hmacKey);                                                                                  //Initialize key
        byte[] hmac = mac.doFinal(message.getBytes());                                                      //Generate HMAC from message
        return Base64.getEncoder().encodeToString(hmac);                                                    //Encode HMAC as string
    }

    public static boolean verifyHMAC(String message, String hmac, SecretKey hmacKey) throws Exception {     //Verifies message integrity
        String computedHMAC = generateHMAC(message, hmacKey);                                               //Takes HMAC from the message
        return MessageDigest.isEqual(computedHMAC.getBytes(), hmac.getBytes());                             //Compare if equal
    }
}
