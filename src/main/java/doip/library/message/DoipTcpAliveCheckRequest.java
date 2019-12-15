package doip.library.message;

import doip.logging.Level;
import doip.logging.LogManager;
import doip.logging.Logger;

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

	@Override
	public void parsePayload(byte[] payload) {
		// Nothing to parse because there is no payload
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] { 0x02, (byte) 0xFD, 0x00, 0x07, 0x00, 0x00, 0x00, 0x00 };
		return message;
	}

}
