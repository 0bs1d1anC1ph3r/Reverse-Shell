package obs1d1anc1ph3r.reverseshell.server.plugins.reactive;

import java.util.HashMap;
import java.util.Map;
import obs1d1anc1ph3r.reverseshell.server.plugins.reactive.utils.ReactiveCommandPlugin;

public class ReactivePluginManager {

	private final Map<String, ReactiveCommandPlugin> pluginsReactive = new HashMap<>();

	public ReactivePluginManager() {
		loadPlugins();
	}

	private void loadPlugins() {
		pluginsReactive.put("screenshot", new ScreenshotServerCommand()); //Made it work like the client
		pluginsReactive.put("file download", new DownloadServerCommand()); //Good job me, this is better
	}

	public ReactiveCommandPlugin getPluginReactive(String commandName) {
		return pluginsReactive.get(commandName.toLowerCase()); //Does the thing
	}

}
