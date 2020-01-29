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

	/**
	 * Will add lookup entries from a list of files. The files
	 * are all located relative to the given path. The function
	 * will just concatenate the path and the filename. 
	 * @param path Path where the files are located
	 * @param files The files which shall be loaded
	 * @throws IOException
	 */
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
		text = text.replaceAll(" ", "");
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
	
	/**
	 * It will iterate over all modifiers and search in the lookup
	 * table if there is a entry where the regex in the table will
	 * match the regex in the modifier. If so, the result in the
	 * lookup table will be changed to the result in the modifier.
	 * @param modifiers List of modifiers which shall be applied 
	 * to the lookup table.
	 */
	public void applyModifiers(List<LookupEntry> modifiers) {
		for (LookupEntry modifier : modifiers) {
			for (LookupEntry entry : this.lookupEntries) {
				if (entry.getRegex().equals(modifier.getRegex())) {
					entry.setResult(modifier.getResult());
				}
			}
		}
	}
	
	/**
	 * Resolves references from a hex string to another hex string. These 
	 * references are in the response and are declared within two brackets '[' and ']'.
	 * The response is referencing bytes from the request. for example the request is
	 * "01 FF AB 07" and the response is "02 00 [1] AA BB" the interpration is that
	 * the response is 5 bytes long and byte 3 shall have the value from byte with index 1 (index begins at 0) 
	 * in the request.
	 * So the final result is "02 00 FF AA BB"
	 * @param request The request message as a hex string
	 * @param response The response message as a hex string which might contain references to
	 * bytes in the response by using brackets, for example "[1]".
	 * @return The response hex string with resolved references.
	 */
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
