package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipTcpAliveCheckResponse extends DoipTcpMessage {

	private static Logger logger = LogManager.getLogger(DoipTcpAliveCheckResponse.class);

	private int sourceAddress = 0;

	private DoipTcpAliveCheckResponse() {
	}

	public DoipTcpAliveCheckResponse(int sourceAddress) {
		this.sourceAddress = sourceAddress;
		if (logger.isInfoEnabled()) {
			log(Level.INFO);
		}
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_ALIVE_RES);
	}
	
	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_ALIVE_RES);
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP alive check response:");
		logger.log(level, "    Source address = " + this.sourceAddress);
		logger.log(level, "----------------------------------------");
	}

	public static DoipTcpAliveCheckResponse createInstance(byte[] payload) {
		DoipTcpAliveCheckResponse doipMessage = new DoipTcpAliveCheckResponse();
		doipMessage.parsePayload(payload);
		doipMessage.log(Level.INFO);
		return doipMessage;
	}

	public void parsePayload(byte[] payload) {
		int high = payload[0] & 0xFF;
		int low = payload[1] & 0xFF;
		this.sourceAddress = (high << 8) | low;
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] { 0x03, (byte) 0xFC, 0x00, 0x08, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00 };
		message[8] = (byte) (sourceAddress >> 8);
		message[9] = (byte) sourceAddress;
		return message;
	}
}
