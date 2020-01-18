package doip.library.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import doip.logging.LogManager;
import doip.logging.Logger;

/**
 * This class holds a sequence of expression-result pairs. The expression is a
 * Java regular expression. With the function "find(String text)" all
 * expressions will be checked if the text matches to one of these expressions.
 * The first expression which matches the text will be returned as a result. If
 * none of the expression matches then null will be returned.
 * 
 */
public class LookupTable {
	
	private static Logger logger = LogManager.getLogger(LookupTable.class);

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
	public void addLookupEntriesFromFile(String filename) throws IOException {
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

	public void addLookupEntriesFromFiles(String path, String[] files) throws IOException {
		for (int i = 0; i < files.length; i++) {
			String fileWithPath = path + files[i];
			this.addLookupEntriesFromFile(fileWithPath);
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
	public String findResultAndApplyModifiers(String text) {
		for (LookupEntry lookupEntry : lookupEntries) {
			if (text.matches(lookupEntry.getRegex())) {
				applyModifiers(lookupEntry.getModifiers());
				
				String result = lookupEntry.getResult();
				result = resolveReferences(text, result);
				return result;
			}
		}

		return null;
	}
	
	/**
	 * Similar to function "String findResult(String text)", but here the parameter
	 * and the return value is a byte array. The argument "bytes" will be first
	 * converted to a hex string, then call findResult(String text), and if the
	 * return value is not null it assumes that the return value is a hex string and
	 * converts back the hex string to a byte array.
	 * 
	 * @param bytes
	 * @return
	 */
	public byte[] findResultAndApplyModifiers(byte[] bytes) {
		String text = Conversion.byteArrayToHexString(bytes);
		String result = this.findResultAndApplyModifiers(text);
		if (result == null)
			return null;
		return Conversion.hexStringToByteArray(result);
	}
	
	public void applyModifiers(List<LookupEntry> modifiers) {
		for (LookupEntry modifier : modifiers) {
			for (LookupEntry entry : this.lookupEntries) {
				if (entry.getRegex().equals(modifier.getRegex())) {
					entry.setResult(modifier.getResult());
				}
			}
		}
	}
	
	public String resolveReferences(String request, String response) {
		logger.trace(">>> public String resolveReferences(String request, String response)");
		logger.debug("request  = " + request);
		logger.debug("response = " + response);
		
		int mode = 0;
		
		char[] responseChars = response.toCharArray();
		int length = responseChars.length;
		StringBuilder result = new StringBuilder(length);
		StringBuilder reference = null;
		
		int i = 0;
		while (i < responseChars.length) {
			logger.debug("Handle character " + i + " = '"+responseChars[i]+"'");
			if (mode == 0) {
				if (responseChars[i] == '[') {
					logger.debug("Switch to mode 1");
					mode = 1;
					
					reference = new StringBuilder();
				} else {
					result.append(responseChars[i]);
				}
			} else {
				if (responseChars[i] == ']') {
					logger.debug("Switch to mode 0");
					mode = 0;
					int index = Integer.parseInt(reference.toString());
					logger.debug("Reference number = " + index);
					if ((index * 2) < request.length()) {
						result.append(request.charAt(index * 2));
						result.append(request.charAt(index * 2 + 1));
					} else {
						result.append("00");
					}
				} else {
					reference.append(responseChars[i]);
				}
			}
			
			i++;
		}
		
		logger.debug("Return " + result.toString());
		
		logger.trace("<<< public String resolveReferences(String request, String response)");
		return result.toString();
	}

	public void addEntry(LookupEntry entry) {
		this.lookupEntries.add(entry);
	}
	
	public LinkedList<LookupEntry> getLookupEntries() {
		return this.lookupEntries;
	}
}
