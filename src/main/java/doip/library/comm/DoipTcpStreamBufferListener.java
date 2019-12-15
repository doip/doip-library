package doip.library.comm;

/**
 * Interface of a listener which will be called from the class DoipStreamBuffer.
 * A listener which implements this interface can register at the DoipStreamBuffer.
 * @author Marco Wehnert
 *
 */
public interface DoipTcpStreamBufferListener {
	/**
	 * Will be called if the sync pattern in the DoIP header is invalid.
	 */
	public void onHeaderIncorrectPatternFormat();
	/**
	 * Will be called if the payload length in the DoIP header exceeds the
	 * maximum message length.
	 */
	public void onHeaderMessageTooLarge();
	
	/**
	 * Will be called if the payload type in the header is unknown.
	 * All payload types which only can occur in a UDP message are also declared as
	 * invalid.
	 */
	public void onHeaderUnknownPayloadType();
	
	/** 
	 * Will be called if the payload length is invalid, e.g. the payload length
	 * does not match the expected payload length for the given payload type.
	 */
	public void onHeaderInvalidPayloadLength();
	
	/** 
	 * Will be called if the payload had been completely received.
	 * @param header The DoIP header of the received message.
	 * @param payloadType The payload type of the received message.
	 * @param payload The payload which had been received.
	 */
	public void onPayloadCompleted(byte[] header, int payloadType, byte[] payload);
	
	/**
	 * Will be called when shredder has completed to shredder bytes.
	 * @param payloadLength Number of bytes which had been shreddered.
	 */
	public void onShredderCompleted(long payloadLength);
	
}
