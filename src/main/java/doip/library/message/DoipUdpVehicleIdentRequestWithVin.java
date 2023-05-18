package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.library.util.Conversion;

public class DoipUdpVehicleIdentRequestWithVin extends DoipUdpMessage {
	
	private static Logger logger = LogManager.getLogger(DoipUdpVehicleIdentRequestWithVin.class);
	
	private byte[] vin = null;

	public DoipUdpVehicleIdentRequestWithVin(byte[] vin) {
		this.vin = vin;
		this.log(Level.INFO);
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP vehicle ident. request with VIN:");
		logger.log(level, "    VIN = " + Conversion.byteArrayToHexString(this.vin));
		logger.log(level, "----------------------------------------");
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_VIR_VIN);
	}
	
	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_VIR_VIN);
	}

	@Override
	public byte[] getMessage() {
		byte[] msg = new byte[25];
		msg[0] = (byte) 0xFF;
		msg[1] = 0x00;
		msg[2] = 0x00;
		msg[3] = 0x03;
		msg[4] = 0x00;
		msg[5] = 0x00;
		msg[6] = 0x00;
		msg[7] = 17;
		
		System.arraycopy(this.vin, 0, msg, 8, 17);
		return msg;
	}

	public byte[] getVin() {
		return vin;
	}

	public void setVin(byte[] vin) {
		this.vin = vin;
	}

}
