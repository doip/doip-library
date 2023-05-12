package doip.library.message;

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.library.util.Conversion;

public class DoipTcpDiagnosticMessage extends DoipTcpMessage {

	private Logger logger = LogManager.getLogger(DoipTcpDiagnosticMessage.class);

	private int sourceAddress = 0;
	private int targetAddress = 0;
	private byte[] diagnosticMessage = new byte[0];

	private DoipTcpDiagnosticMessage() {
	}

	public DoipTcpDiagnosticMessage(int sourceAddress, int targetAddress, byte[] message) {
		this.sourceAddress = sourceAddress;
		this.targetAddress = targetAddress;
		this.diagnosticMessage = message;
		if (logger.isInfoEnabled()) {
			this.log(Level.INFO);
		}
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP diagnostic message:");
		logger.log(level, "    Source address = " + this.sourceAddress);
		logger.log(level, "    Target address = " + this.targetAddress);
		logger.log(level,
				"    Message        = " + Conversion.byteArrayToHexStringShortDotted(this.diagnosticMessage, 64));
		logger.log(level, "----------------------------------------");

	}

	@Override
	public void parsePayload(byte[] payload) {
		int high = payload[0] & 0xFF;
		int low = payload[1] & 0xFF;
		this.sourceAddress = (high << 8) | low;
		high = payload[2] & 0xFF;
		low = payload[3] & 0xFF;
		this.targetAddress = (high << 8) | low;
		this.diagnosticMessage = Arrays.copyOfRange(payload, 4, payload.length);
	}

	public static DoipTcpDiagnosticMessage createInstance(byte[] payload) {
		DoipTcpDiagnosticMessage doipTcpDiagnosticMessage = new DoipTcpDiagnosticMessage();
		doipTcpDiagnosticMessage.parsePayload(payload);
		doipTcpDiagnosticMessage.log(Level.INFO);
		return doipTcpDiagnosticMessage;
	}

	@Override
	public byte[] getMessage() {

		byte[] data = new byte[8 + 4 + diagnosticMessage.length];
		data[0] = 0x02;
		data[1] = (byte) 0xFD;
		data[2] = (byte) 0x80;
		data[3] = 0x01;
		data[4] = (byte) ((diagnosticMessage.length + 4) >> 24);
		data[5] = (byte) ((diagnosticMessage.length + 4) >> 16);
		data[6] = (byte) ((diagnosticMessage.length + 4) >> 8);
		data[7] = (byte) ((diagnosticMessage.length + 4));

		data[8] = (byte) (this.sourceAddress >> 8);
		data[9] = (byte) (this.sourceAddress);
		data[10] = (byte) (this.targetAddress >> 8);
		data[11] = (byte) (this.targetAddress);

		System.arraycopy(diagnosticMessage, 0, data, 12, diagnosticMessage.length);
		return data;
	}

	public int getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(int sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public int getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(int targetAddress) {
		this.targetAddress = targetAddress;
	}

	public byte[] getDiagnosticMessage() {
		return diagnosticMessage;
	}

	public void setDiagnosticMessage(byte[] diagnosticMessage) {
		this.diagnosticMessage = diagnosticMessage;
	}
}
