package org.jumbune.remoting.common;

import java.security.Key;
import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

public class StringUtil {

	static byte[] encodedKey = {-104, 0, 40, 61, -100, -9, -66, -103, 109, -1, -39, -43, 90, 42, 110, 47};
	
	public static String getEncrypted(String plainText) throws Exception{
		try {
			if(plainText == null || plainText.length()==0){
				throw new Exception("Found unacceptable text which trying to encrypt");
			}
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new CustomKey());
			byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));      
			return DatatypeConverter.printBase64Binary(encryptedTextBytes);
		} catch (Exception e) {
			throw new Exception("Can't convert plaintext to encryptedtext", e);
		}
	}

	public static String getPlain(String encryptedText) throws Exception{
		try {
			if(encryptedText == null || encryptedText.length()==0){
				throw new Exception("Found unacceptable text which trying to decrypt");
			}
			byte[] encryptedTextBytes = DatatypeConverter.parseBase64Binary(encryptedText);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new CustomKey());
			byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);	    
			return new String(decryptedTextBytes);
		} catch (javax.crypto.IllegalBlockSizeException e) {
			throw new Exception("Invalid encrypted text", e);
		} catch (Exception e) {
			throw new Exception("Can't convert encryptedtext to plaintext", e);
		}
	}

	public static boolean emptyOrNull(String str){
		return str==null?true:(str.trim().length()==0?true:false);
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