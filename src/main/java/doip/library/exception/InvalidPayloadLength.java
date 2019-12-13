package doip.library.exception;

@SuppressWarnings("serial")
public class InvalidPayloadLength extends DoipException {

	public InvalidPayloadLength() {
	}

	public InvalidPayloadLength(String string) {
		super(string);
	}

}
