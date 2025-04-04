package obs1d1anc1ph3r.reverseshell.server.plugins.proactive;

import java.io.DataOutputStream;
import obs1d1anc1ph3r.reverseshell.server.commandhandling.CommandSender;

public class ExitServerCommand implements ProactiveCommandPlugin {

	@Override
	public void execute(DataOutputStream dataOut, byte[] encryptionKey, CommandSender commandSender, String command) throws Exception {
		commandSender.sendCommand("exit");
		commandSender.cleanup();
		System.out.println("[System] Exiting...");
		System.exit(0);
	}

}
