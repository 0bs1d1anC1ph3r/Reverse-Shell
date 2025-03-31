package obs1d1anc1ph3r.reverseshell.server.encryption;

import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class ECDHKeyExchange {

	private static final int KEY_SIZE = 32;

	public static byte[] generatePrivateKey() {
		byte[] privateKey = new byte[KEY_SIZE];
		new SecureRandom().nextBytes(privateKey);
		return privateKey;
	}

	public static byte[] generatePublicKey(byte[] privateKey) {
		X25519PrivateKeyParameters privateKeyParams = new X25519PrivateKeyParameters(privateKey, 0);
		X25519PublicKeyParameters publicKeyParams = privateKeyParams.generatePublicKey();
		return publicKeyParams.getEncoded();
	}

	public static byte[] performECDHKeyExchange(byte[] privateKey, byte[] peerPublicKey) {
		X25519PrivateKeyParameters privateKeyParams = new X25519PrivateKeyParameters(privateKey, 0);
		X25519PublicKeyParameters peerPublicParams = new X25519PublicKeyParameters(peerPublicKey, 0);

		X25519Agreement agreement = new X25519Agreement();
		agreement.init(privateKeyParams);

		byte[] sharedSecret = new byte[agreement.getAgreementSize()];
		agreement.calculateAgreement(peerPublicParams, sharedSecret, 0);

		byte[] chachaKey = sha256(sharedSecret);

		Arrays.fill(privateKey, (byte) 0);
		Arrays.fill(sharedSecret, (byte) 0);

		return chachaKey;
	}

	private static byte[] sha256(byte[] input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return digest.digest(input);
		} catch (Exception e) {
			throw new RuntimeException("Failed to hash shared secret", e);
		}
	}
}
