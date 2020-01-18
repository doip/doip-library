package doip.library.util;

import java.util.LinkedList;

public class LookupEntry {
	
	private String regex;
	
	private String result;
	
	private LinkedList<LookupEntry> modifiers = new LinkedList<LookupEntry>();
	
	public LookupEntry(String pattern) {
		this.setUp(pattern);
	}
	
	public LookupEntry(String regex, String result) {
		this.regex = regex;
		this.result = result;
	}
	
	/**
	 * Setup a lookup entry by a given string. The string shall contain two strings 
	 * separated by a colon character (=":"). All white spaces will be removed before
	 * splitting the string. A string also can be empty (that is the different way from
	 * standard split function in Java). In the Java split the string "1003:" will only produce
	 * one element. The method which has been implemented here will produce two elements where
	 * the second element it an empty string.
	 * @param text
	 */
	public void setUp(String text) {
		// At first remove all white spaces. White spaces are only there to make the text more readable.
		text = text.replaceAll(" ", "");	
		text = text.replaceAll("\t", "");	
		
		// Now we need to apply a small trick. for example "10 83:".split(":") will only
		// return an array with one element. What we want is strings containing nothing will
		// also produce an element in split function. We replace ":" with " : ", so empty 
		// elements also will produce an element in the result.
		
		text = text.replace(":", " : ");
		
		// Now we create a split
		String[] texts = text.split(":");
		
		// check the number of elements produced by split, it should be two or more entries.
		if (texts.length < 2) {
			throw new IllegalArgumentException("The argument <text> is not well formatted. It shall contain two strings which are separated by a colon character (=':'). But the he given string is " + text);
		}
		
		// Now remove white spaces which had been added before to implement correct behavior of our split.
		this.regex = texts[0].replaceAll(" ", "");
		this.result = texts[1].replaceAll(" ", "");
		
		if (texts.length > 2 ) {
			int numberOfModifiers = (texts.length / 2) - 1;
			for (int i = 0; i < numberOfModifiers; i++) {
				int index = (i + 1) * 2;
				String modifierRegex = texts[index].replaceAll(" ", "");
				String modifierResult = texts[index + 1].replaceAll(" ", "");
				modifiers.add(new LookupEntry(modifierRegex, modifierResult));
			}
		}
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public LinkedList<LookupEntry> getModifiers() {
		return this.modifiers;
	}
}
