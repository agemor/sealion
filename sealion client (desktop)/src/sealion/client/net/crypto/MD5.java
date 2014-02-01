package sealion.client.net.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String digest(String message) {
		String MD5 = "";
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(message.getBytes());
			byte byteData[] = md5.digest();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				buffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			MD5 = buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			MD5 = null;
		}
		return MD5;
	}
}
