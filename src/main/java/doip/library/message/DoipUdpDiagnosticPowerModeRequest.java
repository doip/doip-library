package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipUdpDiagnosticPowerModeRequest extends DoipUdpMessage {
	
	private static Logger logger = LogManager.getLogger(DoipUdpDiagnosticPowerModeRequest.class);
	
	public DoipUdpDiagnosticPowerModeRequest() {
		this.log(Level.INFO);
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP diagnostic power mode request.");
		logger.log(level, "----------------------------------------");
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_DIAG_POWER_MODE_REQ);
	}

	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_DIAG_POWER_MODE_REQ);
	}
	
	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] {0x03, (byte)0xFC, 0x40, 0x03, 0x00, 0x00, 0x00, 0x00};
		return message;
	}
}
