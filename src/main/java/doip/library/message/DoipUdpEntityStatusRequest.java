package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipUdpEntityStatusRequest extends DoipUdpMessage {
	
	private static Logger logger = LogManager.getLogger(DoipUdpEntityStatusRequest.class);
	
	public DoipUdpEntityStatusRequest() {
		this.log(Level.INFO);
	}
	
	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP entity status request.");
		logger.log(level, "----------------------------------------");
	}

	@Override
	public byte[] getMessage() {
		byte[] msg = new byte[] {0x02, (byte) 0xFD, 0x40, 0x01, 0x00, 0x00, 0x00, 0x00};
		return msg;
	}

}
