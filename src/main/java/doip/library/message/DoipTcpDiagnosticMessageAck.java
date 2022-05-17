package doip.library.message;

import java.util.Arrays;

public abstract class DoipTcpDiagnosticMessageAck extends DoipTcpMessage {

	private int sourceAddress = 0;
	private int targetAddress = 0;
	private int ackCode = 0;

	private byte[] diagnosticMessage = new byte[0];

	protected DoipTcpDiagnosticMessageAck() {
	}

	public DoipTcpDiagnosticMessageAck(int sourceAddress, int targetAddress, int ackCode, byte[] message) {
		this.sourceAddress = sourceAddress;
		this.targetAddress = targetAddress;
		this.ackCode = ackCode;
		if (message != null) {
			this.diagnosticMessage = message;
		}
	}

	@Override
	public void parsePayload(byte[] payload) {
		int high = payload[0] & 0xFF;
		int low = payload[1] & 0xFF;
		this.sourceAddress = (high << 8) | low;
		high = payload[2] & 0xFF;
		low = payload[3] & 0xFF;
		this.targetAddress = (high << 8) | low;
		this.ackCode = payload[4] & 0xFF;
		if (payload.length > 5) {
			this.diagnosticMessage = Arrays.copyOfRange(payload, 5, payload.length - 5);
		} else {
			this.diagnosticMessage = new byte[] {};
		}
	}

	@Override
	public byte[] getMessage() {
		byte[] data = new byte[8 + 4 + 1 + diagnosticMessage.length];
		data[0] = 0x02;
		data[1] = (byte) 0xFD;
		data[2] = (byte) 0x80;
		if (this.ackCode == 0) {
			data[3] = 0x02;
		} else {
			data[3] = 0x03;
		}
		data[4] = (byte) ((diagnosticMessage.length + 5) >> 24);
		data[5] = (byte) ((diagnosticMessage.length + 5) >> 16);
		data[6] = (byte) ((diagnosticMessage.length + 5) >> 8);
		data[7] = (byte) ((diagnosticMessage.length + 5));

		data[8] = (byte) (this.sourceAddress >> 8);
		data[9] = (byte) (this.sourceAddress);
		data[10] = (byte) (this.targetAddress >> 8);
		data[11] = (byte) (this.targetAddress);
		data[12] = (byte) (this.ackCode);
		if (diagnosticMessage.length > 0) {
			System.arraycopy(diagnosticMessage, 0, data, 13, diagnosticMessage.length);
		}
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

	public int getAckCode() {
		return ackCode;
	}

	public void setAckCode(int ackCode) {
		this.ackCode = ackCode;
	}
	
	public byte[] getDiagnosticMessage() {
		return diagnosticMessage;
	}

	public void setDiagnosticMessage(byte[] diagnosticMessage) {
		if (diagnosticMessage == null) {
			this.diagnosticMessage = new byte[0];
		} else {
			this.diagnosticMessage = diagnosticMessage;
		}
	}

}
