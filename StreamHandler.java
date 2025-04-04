package obs1d1anc1ph3r.reverseshell.client.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class StreamHandler {

	private final DataInputStream dataIn;
	private final DataOutputStream dataOut;
	private volatile boolean isOn;
	private byte[] encryptionKey;

	public StreamHandler(Socket socket) throws IOException {
		this.dataIn = new DataInputStream(socket.getInputStream());
		this.dataOut = new DataOutputStream(socket.getOutputStream());
		this.isOn = true;
	}

	public DataInputStream getDataIn() {
		return dataIn;
	}

	public DataOutputStream getDataOut() {
		return dataOut;
	}

	//Prove yourself worthy
	public void sendPublicKey(byte[] publicKey) throws IOException {
		dataOut.writeInt(publicKey.length);
		dataOut.write(publicKey);
		dataOut.flush();
	}

	//Okay...but are you worthy?
	public byte[] receivePublicKey() throws IOException {
		int length = dataIn.readInt();
		byte[] key = new byte[length];
		dataIn.readFully(key);
		return key;
	}

	public void close() throws IOException {
		dataIn.close();
		dataOut.close();
	}

	public void setEncryptionKey(byte[] encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public byte[] getEncryptionKey() {
		return encryptionKey;
	}

	public boolean getIsOn() {
		return isOn;
	}

	public void turnOff() {
		isOn = false;
	}

}
