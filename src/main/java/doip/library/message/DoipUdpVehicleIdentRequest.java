package doip.library.message;

import doip.logging.Level;
import doip.logging.LogManager;
import doip.logging.Logger;

public class DoipUdpVehicleIdentRequest extends DoipUdpMessage {

	private static Logger logger = LogManager.getLogger(DoipUdpVehicleIdentRequest.class);

	public DoipUdpVehicleIdentRequest() {
		this.log(Level.INFO);
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP vehicle identification request.");
		logger.log(level, "----------------------------------------");
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00 };
		return message;
	}

}
