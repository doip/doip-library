package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipTcpAliveCheckRequest extends DoipTcpMessage {

	private static Logger logger = LogManager.getLogger(DoipTcpAliveCheckRequest.class);

	public DoipTcpAliveCheckRequest() {
		if (logger.isInfoEnabled()) {
			log(Level.INFO);
		}
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP alive check request");
		logger.log(level, "----------------------------------------");
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_ALIVE_REQ);
	}

	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_ALIVE_REQ);
	}
	
	@Override
	public void parsePayload(byte[] payload) {
		// Nothing to parse because there is no payload
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] { 0x03, (byte) 0xFC, 0x00, 0x07, 0x00, 0x00, 0x00, 0x00 };
		return message;
	}

}
