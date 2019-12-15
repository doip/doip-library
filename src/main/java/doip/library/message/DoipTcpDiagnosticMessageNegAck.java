package doip.library.message;

import doip.logging.Level;
import doip.logging.LogManager;
import doip.logging.Logger;

import doip.library.util.Conversion;

public class DoipTcpDiagnosticMessageNegAck extends DoipTcpDiagnosticMessageAck {

	public static final int NACK_CODE_INVALID_SOURCE_ADDRESS = 0x02;
	public static final int NACK_CODE_UNKNOWN_TARGET_ADDRESS = 0x03;
	public static final int NACK_CODE_DIAGNOSTIC_MESSAGE_TOO_LARGE = 0x04;
	public static final int NACK_CODE_OUT_OF_MEMORY = 0x05;
	public static final int NACK_CODE_TARGET_UNREACHABLE = 0x06;
	public static final int NACK_CODE_UNKNOWN_NETWORK = 0x07;
	public static final int NACK_CODE_TRANSPORT_PROTOCOL_ERROR = 0x08;
	
	private Logger logger = LogManager.getLogger(DoipTcpDiagnosticMessageNegAck.class);
	
	private DoipTcpDiagnosticMessageNegAck() {}

	public DoipTcpDiagnosticMessageNegAck(int sourceAddress, int targetAddress, int ackCode, byte[] message) {
		super(sourceAddress, targetAddress, ackCode, message);
		log(Level.INFO);
	}
	
	public String getNackCodeAsString(int code) {
		switch (code) {
		case 0x02:
			return "0x02 (invalid source address)";
		case 0x03:
			return "0x03 (unknown target address)";
		case 0x04:
			return "0x04 (diagnostic message too large)";
		case 0x05:
			return "0x05 (out of memory)";
		case 0x06:
			return "0x06 (target unreachable)";
		case 0x07:
			return "0x07 (unknown network)";
		case 0x08:
			return "0x08 (transport protocol error)";
		default:
			return String.format("%02X (???)", code);
		}
	}
	
	public void log(Level level) {
		logger.log(level, "----------------------------------------");	
		logger.log(level, "DoIP diagnostic message negative acknowledgement:");
		logger.log(level, "    Source address = " + this.getSourceAddress());
		logger.log(level, "    Target address = " + this.getTargetAddress());
		logger.log(level, "    NACK code      = " + this.getNackCodeAsString(this.getAckCode()));
		logger.log(level, "    Message        = " + Conversion.byteArrayToHexStringShortDotted(this.getDiagnosticMessage(), 64));
		logger.log(level, "----------------------------------------");	
	}

	public static DoipTcpDiagnosticMessageNegAck createInstance(byte[] payload) {
		DoipTcpDiagnosticMessageNegAck doipTcpDiagnosticMessageNegAck = new DoipTcpDiagnosticMessageNegAck();
		doipTcpDiagnosticMessageNegAck.parsePayload(payload);
		doipTcpDiagnosticMessageNegAck.log(Level.INFO);
		return doipTcpDiagnosticMessageNegAck;
	}
}

