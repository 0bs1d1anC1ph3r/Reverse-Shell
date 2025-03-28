package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.security.*;
import java.util.Properties;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Constants {

    private static final String CONFIG_FILE = "config.properties";
    public static final String CONFIG_PATH = "config.properties";
    public static SecretKey AES_SECRET_KEY = loadAESKey();
    public static IvParameterSpec IV = loadIV();

    public static void saveConfig(String key, String value) throws IOException {
        Properties properties = new Properties();
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            }
        }
        properties.setProperty(key, value);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.store(fos, null);
        }
    }

    public static String loadConfig(String key) throws IOException {
        Properties properties = new Properties();
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            }
            return properties.getProperty(key);
        }
        return null;
    }

    public static void saveAESKey(String aesKeyBase64) throws IOException {
        saveConfig("aesKey", aesKeyBase64);
    }

    public static SecretKey loadAESKey() {
        try {
            String aesKeyBase64 = loadConfig("aesKey");
            if (aesKeyBase64 == null) {
                SecretKey newAESKey = CryptoUtils.generateAESKey();
                String aesKeyString = CryptoUtils.secretKeyToBase64(newAESKey);
                saveAESKey(aesKeyString);
                System.out.println("[*] Generated new AES key: " + aesKeyString);
                return newAESKey;
            } else {
                System.out.println("[*] Loaded AES key from config: " + aesKeyBase64);
                return CryptoUtils.base64ToSecretKey(aesKeyBase64);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveIV(String ivBase64) throws IOException {
        saveConfig("iv", ivBase64);
    }

    public static IvParameterSpec loadIV() {
        try {
            String ivBase64 = loadConfig("iv");
            if (ivBase64 == null) {
                IvParameterSpec newIv = CryptoUtils.generateIV();
                String ivString = CryptoUtils.ivToBase64(newIv);
                saveIV(ivString);
                System.out.println("[*] Generated new IV: " + ivString);
                return newIv;
            } else {
                System.out.println("[*] Loaded IV from config: " + ivBase64);
                return CryptoUtils.base64ToIv(ivBase64);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}