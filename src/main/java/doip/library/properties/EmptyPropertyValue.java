package doip.library.properties;

/**
 * Exception which will be thrown when a property 
 * is empty. It will be used by the class PropertyFile.
 * @author Marco Wehnert
 *
 */
@SuppressWarnings("serial")
public class EmptyPropertyValue extends Exception {

	/**
	 * Constructor with the key which was empty.
	 * @param key The k ey which was empty
	 */
	public EmptyPropertyValue(String key) {
		super(key);
	}

}
