package doip.library.exception;

public class DoipException extends Exception {
	
	private static final long serialVersionUID = -1913952490830619880L;
	
	public DoipException(String string) {
		super(string);
	}
	
	public DoipException(Throwable cause) {
		super(cause);
	}
	
	public DoipException(String message, Throwable cause) {
		super(message, cause);
	}
}
