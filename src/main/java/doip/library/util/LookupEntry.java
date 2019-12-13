package doip.library.util;

public class LookupEntry {
	
	private String regex;
	
	private String result;
	
	public LookupEntry(String pattern) {
		this.setUp(pattern);
	}
	
	public void setUp(String text) {
		text = text.replace(" ", "");	
		text = text.replace("\t", "");	
		String[] texts = text.split(":");
		if (texts.length != 2) {
			throw new IllegalArgumentException();
		}
		this.regex = texts[0];
		this.result = texts[1];
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
}
