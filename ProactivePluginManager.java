package obs1d1anc1ph3r.reverseshell.server.plugins.proactive;

import java.util.HashMap;
import java.util.Map;

public class ProactivePluginManager {

	private final Map<String, ProactiveCommandPlugin> pluginsProactive = new HashMap<>();

	public ProactivePluginManager() {
		loadPlugins();
	}

	private void loadPlugins() {
		pluginsProactive.put("upload", new UploadServerCommand());
		pluginsProactive.put("exit", new ExitServerCommand());
	}

	public ProactiveCommandPlugin getPluginProactive(String commandName) {
		return pluginsProactive.get(commandName.toLowerCase()); //Does the thing
	}

}
