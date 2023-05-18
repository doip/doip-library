package doip.library.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

public class DoipUdpDiagnosticPowerModeResponse extends DoipUdpMessage {

	private static Logger logger = LogManager.getLogger(DoipUdpDiagnosticPowerModeResponse.class);

	int diagnsoticPowerMode = 0;

	public DoipUdpDiagnosticPowerModeResponse(int diagnosticPowerMode) {
		this.diagnsoticPowerMode = diagnosticPowerMode;
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_DIAG_POWER_MODE_RES);
	}

	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_DIAG_POWER_MODE_RES);
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP diagnostic power mode response:");
		logger.log(level, "    Diagnostic power mode = " + this.diagnsoticPowerMode);
		logger.log(level, "----------------------------------------");
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] { 0x03, (byte) 0xFC, 0x40, 0x04, 0x00, 0x00, 0x00, 0x01, 0x00 };
		message[8] = (byte) this.diagnsoticPowerMode;
		return message;
	}

}
