package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipTcpHeaderNegAck extends DoipTcpMessage implements DoipHeaderNegAck {

	private static Logger logger = LogManager.getLogger(DoipTcpHeaderNegAck.class);

	private int code = -1;

	private DoipTcpHeaderNegAck() {
	}

	public DoipTcpHeaderNegAck(int code) {
		this.code = code;
		this.log(Level.INFO);
	}

	public static DoipTcpHeaderNegAck createInstance(byte[] payload) {
		DoipTcpHeaderNegAck doipHeaderNegAck = new DoipTcpHeaderNegAck();
		doipHeaderNegAck.parsePayload(payload);
		doipHeaderNegAck.log(Level.INFO);
		return doipHeaderNegAck;
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_HEADER_NACK);
	}

	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_HEADER_NACK);
	}
	
	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP header negative acknowledgement (TCP):");
		logger.log(level, "    Code = " + String.format("0x%02X (", code) + getCodeAsString() + ")");
		logger.log(level, "----------------------------------------");
	}
	
	public String getCodeAsString() {
		switch (code) {
		case 0x00:
			return "incorrect pattern format";
		case 0x01:
			return "unknown payload type";
		case 0x02:
			return "message too large";
		case 0x03:
			return "out of memory";
		case 0x04:
			return "invalid payload length";
		default:
			return "reserved by this document";
		}
	}

	public int getCode() {
		return code;
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] { 0x03, (byte) 0xFC, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00 };
		message[8] = (byte) (code & 0xFF);
		return message;
	}

	public void parsePayload(byte[] payload) {
		this.code = payload[0] & 0xFF;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
