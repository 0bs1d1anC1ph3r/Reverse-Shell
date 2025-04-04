package obs1d1anc1ph3r.reverseshell.server.encryption;

import java.io.*;
import java.util.logging.*;

public class SecureConnection {
    private static final Logger logger = Logger.getLogger(SecureConnection.class.getName());
    private final DataInputStream dataIn;
    private final DataOutputStream dataOut;

    public SecureConnection(DataInputStream dataIn, DataOutputStream dataOut) {
        this.dataIn = dataIn;
        this.dataOut = dataOut;
    }

    public byte[] establishSecureConnection() throws IOException {
        try {
            byte[] serverPrivateKey = ECDHKeyExchangeServer.generatePrivateKey(); //Nom nom nom (because it's a byte, like a bite, so nom nom nom)
            byte[] serverPublicKey = ECDHKeyExchangeServer.generatePublicKey(serverPrivateKey); //Maybe I should get diagnosed for autism, I'm pretty sure I'd pass -- Yeah, probably

            dataOut.writeInt(serverPublicKey.length); //Yeah, do that thing
            dataOut.write(serverPublicKey); //Oh yeah
            dataOut.flush(); //Always flush

            int clientKeyLength = dataIn.readInt(); //Sexy key -- I was too tired to think of something to put here yesterday
            byte[] clientPublicKey = new byte[clientKeyLength]; //I'm losing my mind
            dataIn.readFully(clientPublicKey); //Oh, I forgot that I should also be using these comments to actually document what the stuff does -- We know

            byte[] encryptionKey = ECDHKeyExchangeServer.performECDHKeyExchange(serverPrivateKey, clientPublicKey); //This method does the encryption handshake stuff
            logger.info("[-] Secure connection established using ECDH and ChaCha20 encryption."); //Yeah, you didn't understand is super well when putting it in here, so you probably wont in the future
            return encryptionKey; //Unless you actually improve, which you might
        } catch (IOException e) { //I'm talking to myself, in case anyone who isn't me is reading this. Also if you're anyone else reading this, I am so fucking sorry.
            throw new IOException("Secure key exchange failed: " + e.getMessage(), e); //Honestly if you're me reading this, I'm also really sorry, but also you kind of deserve it
        }
    }
}
