package sealion.client.net.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
	public static String digest(String message) {
		String SHA = "";
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			sha256.update(message.getBytes());
			byte byteData[] = sha256.digest();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				buffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			SHA = null;
		}
		return SHA;
	}
}
