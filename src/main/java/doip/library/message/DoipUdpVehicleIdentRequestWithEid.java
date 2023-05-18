package doip.library.message;

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.library.util.Conversion;

public class DoipUdpVehicleIdentRequestWithEid extends DoipUdpMessage {
	
	private static Logger logger = LogManager.getLogger(DoipUdpVehicleIdentRequestWithEid.class);
	
	private byte[] eid;

	public DoipUdpVehicleIdentRequestWithEid(byte[] eid) {
		this.eid = Arrays.copyOf(eid, 6);
		this.log(Level.INFO);
	}
	
	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP vehicle ident. request with EID");
		logger.log(level, "    EID = " + Conversion.byteArrayToHexString(this.eid));
		logger.log(level, "----------------------------------------");
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_VIR_EID);
	}

	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_VIR_EID);
	}

	@Override
	public byte[] getMessage() {
		byte[] msg = new byte[14];
		msg[0] = (byte) 0xFF;
		msg[1] = 0x00;
		msg[2] = 0x00;
		msg[3] = 0x02;
		msg[4] = 0x00;
		msg[5] = 0x00;
		msg[6] = 0x00;
		msg[7] = 6;
		
		System.arraycopy(this.eid, 0, msg, 8, 6);
		return msg;
	}

	public byte[] getEid() {
		return eid;
	}

	public void setEid(byte[] eid) {
		this.eid = eid;
	}

}
