package obs1d1anc1ph3r.reverseshell.server.plugins;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public interface CommandPlugin {

	void execute(DataInputStream dataIn, DataOutputStream dataOut, byte[] encryptionKey, Socket clientSocket) throws Exception;
}


//https://www.youtube.com/watch?v=dQw4w9WgXcQ
