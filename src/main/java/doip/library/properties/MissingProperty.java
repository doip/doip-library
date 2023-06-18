package doip.library.properties;

/**
 * Exception which will be thrown when a key is 
 * expected to exist in a property file but it was not defined.
 * @author Marco Wehnert
 *
 */
@SuppressWarnings("serial")
public class MissingProperty extends Exception {
	
	/**
	 * Constructor with the key
	 * @param key The key which was expected to exist in the property file.
	 */
	public MissingProperty(String key) {
		super("The  property \"" +key+"\" is missing");
	}

}
