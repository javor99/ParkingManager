package com.parkingmanager.parkingmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EmailEncryption {
    // Define the encryption key as a hexadecimal string
    private final Environment environment;

    @Autowired
    public EmailEncryption(Environment environment) {
        this.environment = environment;
        ENCRYPTION_KEY_HEX = environment.getProperty("encryption.key");
    }
    private String ENCRYPTION_KEY_HEX;




    public String encrypt(String email) throws Exception {
        byte[] keyBytes = hexStringToByteArray(ENCRYPTION_KEY_HEX);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(email.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public  String decrypt(String encryptedEmail) throws Exception {
        byte[] keyBytes = hexStringToByteArray(ENCRYPTION_KEY_HEX);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedEmail);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // Helper method to convert a hexadecimal string to byte array
    private  byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }


}
