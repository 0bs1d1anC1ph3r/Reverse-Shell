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
		plugins.put("screenshot", new ScreenshotCommand());
		plugins.put("file download", new DownloadCommand());
	}

	public CommandPlugin getPlugin(String commandName) {
		return plugins.get(commandName.toLowerCase());
	}

	public Map<String, CommandPlugin> getPlugins() {
		return Collections.unmodifiableMap(plugins);
	}
}
