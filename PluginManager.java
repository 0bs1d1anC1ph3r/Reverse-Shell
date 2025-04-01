package obs1d1anc1ph3r.reverseshell.server.plugins;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginManager {

	private final Map<String, CommandPlugin> plugins = new HashMap<>();

	public PluginManager() {
		loadPlugins();
	}

	private void loadPlugins() {
		plugins.put("screenshot", new ScreenshotCommand()); //Made it work like the client
		plugins.put("file download", new DownloadCommand()); //Good job me, this is better
	}

	public CommandPlugin getPlugin(String commandName) {
		return plugins.get(commandName.toLowerCase()); //Does the thing
	}

	public Map<String, CommandPlugin> getPlugins() {
		return Collections.unmodifiableMap(plugins); //Returns the thing
	}
}
