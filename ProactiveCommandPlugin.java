package obs1d1anc1ph3r.reverseshell.server.plugins.proactive;

import java.io.DataOutputStream;
import obs1d1anc1ph3r.reverseshell.server.commandhandling.CommandSender;

public interface ProactiveCommandPlugin {

	void execute(DataOutputStream dataOut, byte[] encryptionKey, CommandSender commandSender, String command) throws Exception;
}


//https://www.youtube.com/watch?v=dQw4w9WgXcQ
