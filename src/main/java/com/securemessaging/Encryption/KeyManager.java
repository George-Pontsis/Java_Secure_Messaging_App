package com.securemessaging.Encryption;

import javax.crypto.*;                                                                                       //Provides cryptographic operations (AES, Cipher)
import javax.crypto.spec.*;                                                                                  //Provides key and parameters specification
import java.security.*;                                                                                      //Core security classes (RSA keys, key repair
import java.security.spec.*;                                                                                 //Key specification utilities
import java.util.Base64;                                                                                     //Encode binary data to text

public class KeyManager {                                                                                    //Class for Creation and protection of encryption keys
    private static final String RSA_ALGO = "RSA";                                                            //Algorithm name for RSA encryption
    private static final int RSA_KEY_SIZE = 2048;                                                            //RSA key size
    private static final String AES_ALGO = "AES/GCM/NoPadding";                                              //AES mode
    private static final int AES_KEY_SIZE = 256;                                                             //Strong AES key size for symmetric encryption
    private static final int GCM_IV_LENGTH = 12;                                                             //Standard IV length for AES-GCM

    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {                             //Generates a public/private RSA key
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGO);                                    //Generator for RSA keys
        keyGen.initialize(RSA_KEY_SIZE);                                                                     //set RSA key size
        return keyGen.generateKeyPair();                                                                     //Generate and return key
    }

    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {                               //Generates AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");                                      //Generator for AES keys
        keyGen.init(AES_KEY_SIZE);                                                                           //Set AES key size
        return keyGen.generateKey();                                                                         //Generate and return key
    }

    public static String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey rsaPublicKey) throws Exception {   //Encrypts AES key using RSA public key
        Cipher cipher = Cipher.getInstance(RSA_ALGO);                                                        //Create RSA cipher
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);                                                      //Initialize cipher for encryption
        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());                                           //Encrypt raw AES bytes
        return Base64.getEncoder().encodeToString(encryptedKey);                                             //Encode binary data to text and return encrypted AES key as string
    }

    public static SecretKey decryptAESKeyWithRSA(String encryptedAESKey, PrivateKey rsaPrivateKey) throws Exception {   //Decrypts AES key using RSA Private key
        Cipher cipher = Cipher.getInstance(RSA_ALGO);                                                        //Create RSA cipher
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);                                                     //Initialize cipher for decryption
        byte[] decryptedKey = cipher.doFinal(Base64.getDecoder().decode(encryptedAESKey));                   //Decrypt AES bytes
        return new SecretKeySpec(decryptedKey, "AES");                                              //Returns AES from the decrypted bytes
    }
}
