package com.securemessaging.Encryption;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;

public class MessageEncryptor {
    private static final String AES_ALGO = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

    public static String encryptMessage(String message, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGO);
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] encrypted = cipher.doFinal(message.getBytes());
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(result);
    }

    public static String decryptMessage(String encryptedMessage, SecretKey aesKey) throws Exception {
        byte[] data = Base64.getDecoder().decode(encryptedMessage);
        byte[] iv = new byte[12];
        System.arraycopy(data, 0, iv, 0, iv.length);
        byte[] encrypted = new byte[data.length - iv.length];
        System.arraycopy(data, iv.length, encrypted, 0, encrypted.length);
        Cipher cipher = Cipher.getInstance(AES_ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
        return new String(cipher.doFinal(encrypted));
    }

    public static String generateHMAC(String message, SecretKey hmacKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(hmacKey);
        byte[] hmac = mac.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(hmac);
    }

    public static boolean verifyHMAC(String message, String hmac, SecretKey hmacKey) throws Exception {
        String computedHMAC = generateHMAC(message, hmacKey);
        return MessageDigest.isEqual(computedHMAC.getBytes(), hmac.getBytes());
    }
}
