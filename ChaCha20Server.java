package obs1d1anc1ph3r.reverseshell.server.encryption;

import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class ChaCha20Server {

	private static final int NONCE_SIZE = 12;
	private static final int KEY_SIZE = 32;

	public static byte[] encrypt(byte[] key, byte[] nonce, byte[] message) throws Exception { //Encrypt that shit
		if (key.length != KEY_SIZE || nonce.length != NONCE_SIZE) { //Pedofile, not hat, you learn something new every day
			throw new IllegalArgumentException("Invalid key or nonce length");
		}

		Cipher cipher = Cipher.getInstance("ChaCha20"); //ChaCha is a great name
		ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonce, 1);
		SecretKeySpec keySpec = new SecretKeySpec(key, "ChaCha20");

		cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
		return cipher.doFinal(message);
	}

	public static byte[] decrypt(byte[] key, byte[] nonce, byte[] ciphertext) throws Exception { //Decrypt that shit
		if (key.length != KEY_SIZE || nonce.length != NONCE_SIZE) {
			throw new IllegalArgumentException("Invalid key or nonce length");
		}

		Cipher cipher = Cipher.getInstance("ChaCha20");
		ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonce, 1);
		SecretKeySpec keySpec = new SecretKeySpec(key, "ChaCha20");

		cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
		return cipher.doFinal(ciphertext);
	}

	public static byte[] generateNonce() { //Yeah, do the thing
		byte[] nonce = new byte[NONCE_SIZE];
		new SecureRandom().nextBytes(nonce);
		return nonce;
	}

	public static byte[] stringToBytes(String str) {
		return str.getBytes(StandardCharsets.UTF_8);
	}

	public static String bytesToString(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

}
