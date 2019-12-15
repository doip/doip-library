package doip.library.message;

import doip.logging.LogManager;
import doip.logging.Logger;
import doip.logging.Level;

public class DoipUdpDiagnosticPowerModeResponse extends DoipUdpMessage {

	private static Logger logger = LogManager.getLogger(DoipUdpDiagnosticPowerModeResponse.class);

	int diagnsoticPowerMode = 0;

	public DoipUdpDiagnosticPowerModeResponse(int diagnosticPowerMode) {
		this.diagnsoticPowerMode = diagnosticPowerMode;
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP diagnostic power mode response:");
		logger.log(level, "    Diagnostic power mode = " + this.diagnsoticPowerMode);
		logger.log(level, "----------------------------------------");
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] { 0x02, (byte) 0xFD, 0x40, 0x04, 0x00, 0x00, 0x00, 0x01, 0x00 };
		message[8] = (byte) this.diagnsoticPowerMode;
		return message;
	}

}
