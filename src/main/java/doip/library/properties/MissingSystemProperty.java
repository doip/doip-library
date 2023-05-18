package doip.library.properties;

public class MissingSystemProperty extends Exception {

	private static final long serialVersionUID = 839224216192327460L;
	
	public MissingSystemProperty(String key) {
		super("The  systemproperty \"" +key+"\" is missing");
	}
}
