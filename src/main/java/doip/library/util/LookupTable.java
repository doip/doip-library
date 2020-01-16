package doip.library.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class holds a sequence of expression-result pairs. The expression is a
 * Java regular expression. With the function "find(String text)" all
 * expressions will be checked if the text matches to one of these expressions.
 * The first expression which matches the text will be returned as a result. If
 * none of the expression matches then null will be returned.
 * 
 * @author Marco Wehnert
 *
 */
public class LookupTable {

	LinkedList<LookupEntry> lookupEntries = new LinkedList<LookupEntry>();

	/**
	 * Loads the table from a file. ATTENTION: The purpose of this table is handle
	 * byte arrays represented by a hex string, e.g. "01 02 AB 40". For better
	 * readability it shall be allowed to add spaces between each byte. The
	 * separator between an expression and a result is the colon character ":". When
	 * data will be read from a file all spaces within one line will be removed.
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void appendLookupEntriesFromFile(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		while (line != null) {
			if (!line.startsWith("#")) {
				if (line.contains(":")) {
					lookupEntries.add(new LookupEntry(line));
				}
			}
			line = br.readLine();
		}
		br.close();
	}

	public void appendLookupEntriesFromFiles(String path, String[] files) throws IOException {
		for (int i = 0; i < files.length; i++) {
			String fileWithPath = path + files[i];
			this.appendLookupEntriesFromFile(fileWithPath);
		}
	}

	/**
	 * Searches in all lookup entries where the given text matches to the regular
	 * expression in the lookup entry. If one entry matches then it will return the
	 * result of the lookup entry. If no entry would match it returns null.
	 * 
	 * @param text Text which needs to match the regular expression
	 * @return Returns the result if a entry could be found where the text matches
	 *         the regular expression. If no entry will match it returns null.
	 */
	public String findResult(String text) {
		for (LookupEntry lookupEntry : lookupEntries) {
			if (text.matches(lookupEntry.getRegex())) {
				return lookupEntry.getResult();
			}
		}

		return null;
	}

	/**
	 * Simular to functon "String findResult(String text)", but here the parameter
	 * and the return value is a byte array. The argument "bytes" will be first
	 * converted to a hex string, then call findResult(String text), and if the
	 * return value is not null it assumes that the return value is a hex string and
	 * converts back the hex string to a byte array.
	 * 
	 * @param bytes
	 * @return
	 */
	public byte[] findResult(byte[] bytes) {
		String text = Conversion.byteArrayToHexString(bytes);
		String result = this.findResult(text);
		if (result == null)
			return null;
		return Conversion.hexStringToByteArray(result);
	}
}
