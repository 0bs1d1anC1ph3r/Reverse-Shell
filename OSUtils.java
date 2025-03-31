package obs1d1anc1ph3r.reverseshell;

public class OSUtils {

	//Windows bad
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	public static String getShell() {
		return isWindows() ? "cmd.exe" : "sh";
	}

	public static String getShellFlag() {
		return isWindows() ? "/c" : "-c";
	}
}
