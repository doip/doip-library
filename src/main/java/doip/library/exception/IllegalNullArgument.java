package doip.library.exception;

/**
 * Convenience exception class which shall be thrown if the argument
 * of a method is null.
 */
public class IllegalNullArgument extends java.lang.IllegalArgumentException {

	private static final long serialVersionUID = 2712038578737726245L;
	
	public IllegalNullArgument(String argument, String method) {
		super("The argument <" + argument + "> in method <" + method + "> is null.");
	}
}
