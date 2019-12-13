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

	LinkedList<LookupEntry> patterns = new LinkedList<LookupEntry>();

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
	public void appendPatterns(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		while (line != null) {
			if (!line.startsWith("#")) {
				if (line.contains(":")) {
					patterns.add(new LookupEntry(line));
				}
			}
			line = br.readLine();
		}
		br.close();
	}

	public void appendPatterns(String path, String[] files) throws IOException {
		for (int i = 0; i < files.length; i++) {
			String fileWithPath = path + files[i];
			this.appendPatterns(fileWithPath);
		}
	}

	public String findResult(String text) {
		Iterator<LookupEntry> iter = patterns.iterator();
		while (iter.hasNext()) {
			LookupEntry pattern = iter.next();
			if (text.matches(pattern.getRegex())) {
				return pattern.getResult();
			}
		}
		return null;
	}

	public byte[] findResult(byte[] bytes) {
		String text = Conversion.byteArrayToHexString(bytes);
		String result = this.findResult(text);
		if (result == null)
			return null;
		return Conversion.hexStringToByteArray(result);
	}
}
