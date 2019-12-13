package doip.library.message;

public interface DoipHeaderNegAck {
	
	public static final int NACK_INCORRECT_PATTERN_FORMAT = 0;
	public static final int NACK_UNKNOWN_PAYLOAD_TYPE = 1;
	public static final int NACK_MESSAGE_TOO_LARGE = 2;
	public static final int NACK_OUT_OF_MEMORY = 3;
	public static final int NACK_INVALID_PAYLOAD_LENGTH = 4;

}
