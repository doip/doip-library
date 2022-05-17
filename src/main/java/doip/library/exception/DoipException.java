package doip.library.exception;

@SuppressWarnings("serial")
public class DoipException extends Exception {
	
	public DoipException() {
		
	}
	
	public DoipException(String string) {
		super(string);
	}
	
	public DoipException(String message, Throwable cause) {
		super(message, cause);
	}
}
