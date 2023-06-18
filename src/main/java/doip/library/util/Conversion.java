package doip.library.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import doip.library.exception.UnsupportedEncoding;

/**
 * This class converts data from one type to another type, e.g. a byte array to
 * an ascii string.
 * 
 * @author Marco Wehnert
 *
 */
public class Conversion {

	/**
	 * char array which will be used for conversion of data.
	 */
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static byte[] hexStringToByteArray(String s) {
		s = s.replaceAll("0x" , "");
		
		// Following section can be optimized by using char[]
		s = s.replaceAll(" ", "");
		s = s.replaceAll(",", "");
		s = s.replaceAll("\\.", "");
		s = s.replaceAll(";", "");
		//-----------------------------------
		
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Converts a byte array to an hex string.
	 * @param bytes The byte array which shall be converted
	 * @return The hex string
	 */
	public static String byteArrayToHexString(byte[] bytes) {
		return byteArrayToHexStringShort(bytes, bytes.length);
	}
	
	/**
	 * Converts a byte array to a hex string but it will
	 * take only 'count' bytes. If the byte array is smaller than
	 * 'count' the byte array will be converted as it is.
	 * @param bytes The byte array which shall be converted
	 * @param count The maximum number of bytes which shall be converted
	 * @return The hex string
	 */
	public static String byteArrayToHexStringShort(byte[] bytes, int count) {
		if (bytes.length == 0) return "";
		if (count == 0) return "";
		if (bytes.length < count) count = bytes.length;
		
		// Don't call byteArrayToHexString because that will require
		// to create a new byte array with reduced size and this can be
		// time consuming
		char[] hexChars = new char[count * 3 - 1];
		for (int j = 0; j < count; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			if (j < count - 1) {
				hexChars[j * 3 + 2] = ' ';
			}
		}
		return new String(hexChars);		
	}
	
	public static String byteArrayToHexStringShortDotted(byte[] bytes, int count) {
		if (bytes.length <= count) {
			return byteArrayToHexString(bytes);
		} else {
			return byteArrayToHexStringShort(bytes, count) + "...";
		}
	}
	
	public static String byteArrayToAsciiString(byte[] bytes) throws UnsupportedEncoding {
		String string;
		try {
			string = new String(bytes, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedEncoding();
		}
		return string;
	}
	
	public static byte[] asciiStringToByteArray(String text) {
		return text.getBytes(StandardCharsets.ISO_8859_1);
	}
}
