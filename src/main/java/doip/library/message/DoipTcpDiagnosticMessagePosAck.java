package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.library.util.Conversion;

public class DoipTcpDiagnosticMessagePosAck extends DoipTcpDiagnosticMessageAck {
	
	private static Logger logger = LogManager.getLogger(DoipTcpDiagnosticMessagePosAck.class);
	
	private DoipTcpDiagnosticMessagePosAck() {}

	public DoipTcpDiagnosticMessagePosAck(int sourceAddress, int targetAddress, int ackCode, byte[] message) {
		super(sourceAddress, targetAddress, ackCode, message);
		log(Level.INFO);
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_DIAG_MESSAGE_POS_ACK);
	}
	
	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_DIAG_MESSAGE_POS_ACK);
	}
	
	public void log(Level level) {
		logger.log(level, "----------------------------------------");	
		logger.log(level, "DoIP diagnostic message positive acknowledgement:");
		logger.log(level, "    Source address = " + this.getSourceAddress());
		logger.log(level, "    Target address = " + this.getTargetAddress());
		logger.log(level, "    ACK code       = " + this.getAckCode());
		logger.log(level, "    Message        = " + Conversion.byteArrayToHexStringShortDotted(this.getDiagnosticMessage(), 64));
		logger.log(level, "----------------------------------------");	
	}


	public static DoipTcpDiagnosticMessagePosAck createInstance(byte[] payload) {
		DoipTcpDiagnosticMessagePosAck doipTcpDiagnosticMessagePosAck = new DoipTcpDiagnosticMessagePosAck();
		doipTcpDiagnosticMessagePosAck.parsePayload(payload);
		doipTcpDiagnosticMessagePosAck.log(Level.INFO);
		return doipTcpDiagnosticMessagePosAck;
	}
}
