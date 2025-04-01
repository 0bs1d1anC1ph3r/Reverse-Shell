package obs1d1anc1ph3r.reverseshell.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSetup {

	private static final Logger logger = Logger.getLogger(ServerSetup.class.getName());
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private BufferedReader userInput;
	private final int port;

	public ServerSetup(int port) {
		this.port = port;
	}

	public void setupServer() throws IOException {
		serverSocket = new ServerSocket(port);
		logger.log(Level.INFO, "[-] Server is running on port {0}", port);
	}

	public Socket waitForConnection() throws IOException {
		logger.info("[-] Waiting for incoming connection...");
		clientSocket = serverSocket.accept();
		logger.log(Level.INFO, "[-*] Connection established with {0}", clientSocket.getInetAddress());

		dataIn = new DataInputStream(clientSocket.getInputStream());
		dataOut = new DataOutputStream(clientSocket.getOutputStream());
		userInput = new BufferedReader(new InputStreamReader(System.in));

		return clientSocket;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public DataInputStream getDataInputStream() {
		return dataIn;
	}

	public DataOutputStream getDataOutputStream() {
		return dataOut;
	}

	public BufferedReader getUserInput() {
		return userInput;
	}
}
