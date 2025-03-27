package obs1d1anc1ph3r.reverseshell;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class CryptoUtils {

    private static final String AES_MODE = "AES/CBC/PKCS5Padding";
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final int IV_SIZE = 16; // AES block size

    // AES Encryption
    public static String encryptAES(String data, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        System.out.println("Raw Data before Encryption: " + data); // Debug log
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedData);
        System.out.println("Encrypted Data (Base64): " + encryptedBase64); // Debug log
        return encryptedBase64;
    }

    // AES Decryption
    public static String decryptAES(String encryptedData, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        System.out.println("Encrypted Data (Base64) before Decryption: " + encryptedData); // Debug log
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decryptedData = cipher.doFinal(decodedData);
        String decryptedString = new String(decryptedData, StandardCharsets.UTF_8);
        System.out.println("Decrypted Data: " + decryptedString); // Debug log
        return decryptedString;
    }

    // RSA Encryption
    public static String encryptRSA(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // RSA Decryption
    public static String decryptRSA(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_MODE);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    // Generate AES Key
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    // Generate RSA Key Pair
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    // Convert SecretKey to Base64
    public static String secretKeyToBase64(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // Convert Base64 to SecretKey
    public static SecretKey base64ToSecretKey(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // Convert RSA to AES
    public static SecretKey decryptRSAToAESKey(byte[] encryptedAESKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_MODE);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedAESKeyBytes = cipher.doFinal(encryptedAESKey);
        return new SecretKeySpec(decryptedAESKeyBytes, "AES");
    }

    // Convert a Base64-encoded string to a PublicKey
    public static PublicKey getPublicKeyFromBase64(String base64) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // Convert a Base64-encoded string to a PrivateKey
    public static PrivateKey getPrivateKeyFromBase64(String base64) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    // Generate a new IV
    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    // Convert IV to Base64
    public static String ivToBase64(IvParameterSpec iv) {
        return Base64.getEncoder().encodeToString(iv.getIV());
    }

    // Convert Base64 to IV
    public static IvParameterSpec base64ToIv(String base64Iv) {
        byte[] decodedIv = Base64.getDecoder().decode(base64Iv);
        return new IvParameterSpec(decodedIv);
    }

    public static PrivateKey getPrivateKeyFromEnv() throws Exception {
        String privateKeyBase64 = System.getenv("PRIVATE_KEY_BASE64");
        return getPrivateKeyFromBase64(privateKeyBase64);
    }
}
