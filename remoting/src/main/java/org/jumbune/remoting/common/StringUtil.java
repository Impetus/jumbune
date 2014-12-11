package org.jumbune.remoting.common;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

public class StringUtil {

	static byte[] encodedKey = {-104, 0, 40, 61, -100, -9, -66, -103, 109, -1, -39, -43, 90, 42, 110, 47};
	
	public static String getEncrypted(String plainText) {
		Cipher cipher = null;
	    byte[] encryptedTextBytes = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new CustomKey());
			encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}        
	    return DatatypeConverter.printBase64Binary(encryptedTextBytes);
	}

	public static String getPlain(String encryptedText) {
		byte[] encryptedTextBytes = DatatypeConverter.parseBase64Binary(encryptedText);
	    Cipher cipher;
	    byte[] decryptedTextBytes = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	       
	    cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, new CustomKey());
	    decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}	    
	    return new String(decryptedTextBytes);	
	}
    
    private static class CustomKey implements Key{

		/**
		 * Generated Serial Version UID
		 */
		private static final long serialVersionUID = -6313218065709713525L;

		@Override
		public String getFormat() {
			return "RAW";
		}
		
		@Override
		public byte[] getEncoded() {
			return encodedKey;
		}
		
		@Override
		public String getAlgorithm() {
			return "AES";
		}
    }
}