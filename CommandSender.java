package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;

public class CommandSender implements Runnable {

    private final DataOutputStream dataOut;
    private final BufferedReader userInput;
    private final ReverseShellServer server;

    public CommandSender(DataOutputStream dataOut, BufferedReader userInput, ReverseShellServer server) {
        this.dataOut = dataOut;
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
        try {
            if (dataOut != null) {
                dataOut.writeUTF(command);
                dataOut.flush();
                System.out.println("[-] Command sent: " + command);
            } else {
                System.err.println("[ERROR] Output stream is closed. Unable to send command.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error sending command: " + e.getMessage());
        }
    }

    private void cleanup() {
        try {
            if (dataOut != null) {
                dataOut.close();
                System.out.println("[-] Output stream closed.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error during cleanup: " + e.getMessage());
        }
    }
}
