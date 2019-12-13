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

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP header negative acknowledgement (TCP):");
		logger.log(level, "    Code = " + this.code);
		logger.log(level, "");
		logger.log(level, "----------------------------------------");
	}

	public int getCode() {
		return code;
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[] { 0x02, (byte) 0xFD, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00 };
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
