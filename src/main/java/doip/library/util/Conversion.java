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
		s = s.replaceAll(" ", "");
		s = s.replaceAll("0x" , "");
		s = s.replaceAll(",", "");
		s = s.replaceAll(";", "");
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Converts a byte array to an ascii string.
	 * @param bytes The byte array which shall be converted
	 * @return The ascii string
	 */
	public static String byteArrayToHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static String byteArrayToHexStringShort(byte[] bytes, int count) {
		if (bytes.length <= count) {
			return byteArrayToHexString(bytes);
		} else {
			char[] hexChars = new char[count * 2];
			for (int j = 0; j < count; j++) {
				int v = bytes[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			}
			return new String(hexChars);		}
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
