package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipTcpRoutingActivationRequest extends DoipTcpMessage {

	private static Logger logger = LogManager.getLogger(DoipTcpRoutingActivationRequest.class);

	int sourceAddress = 0;
	int activationType = 0;
	long oemData = -1; // -1 = No data is there

	public DoipTcpRoutingActivationRequest(int sourceAddress, int activationType, long oemData) {
		this.sourceAddress = sourceAddress;
		this.activationType = activationType;
		this.oemData = oemData;
		this.log(Level.INFO);
	}

	private DoipTcpRoutingActivationRequest() {
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_ROUTING_REQ);
	}
	
	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_ROUTING_REQ);
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP routing activation request:");
		logger.log(level, "    Source address  = " + this.sourceAddress);
		logger.log(level, "    Activation Type = " + this.activationType);
		logger.log(level, "    OEM data        = " + this.getOemData());
		logger.log(level, "----------------------------------------");
	}

	@Override
	public void parsePayload(byte[] payload) {
		int high = payload[0] & 0xFF;
		int low = payload[1] & 0xFF;
		this.sourceAddress = (high << 8) | low;
		this.activationType = payload[2] & 0xFF;
		if (payload.length == 11) {
			long highhigh = payload[3] & 0xFF;
			long highlow = payload[4] & 0xFF;
			long lowhigh = payload[5] & 0xFF;
			long lowlow = payload[6] & 0xFF;
			this.oemData = (highhigh << 24) | (highlow << 16) | (lowhigh << 8) | lowlow;
		}
	}

	public static DoipTcpRoutingActivationRequest createInstance(byte[] payload) {
		DoipTcpRoutingActivationRequest doipMessage = new DoipTcpRoutingActivationRequest();
		doipMessage.parsePayload(payload);
		doipMessage.log(Level.INFO);
		return doipMessage;
	}

	@Override
	public byte[] getMessage() {
		byte[] message;
		if (oemData == -1) {
			message = new byte[8 + 7];
			message[7] = 7;
		} else {
			message = new byte[8 + 11];
			message[7] = 11;
			message[15] = (byte) (this.oemData >> 24);
			message[16] = (byte) (this.oemData >> 16);
			message[17] = (byte) (this.oemData >> 8);
			message[18] = (byte) (this.oemData);
		}

		message[0] = 0x03;
		message[1] = (byte) 0xFC;
		message[2] = 0x00;
		message[3] = 0x05;
		message[4] = 0x00;
		message[5] = 0x00;
		message[6] = 0x00;

		message[8] = (byte) (this.sourceAddress >> 8);
		message[9] = (byte) (this.sourceAddress);
		message[10] = (byte) this.activationType;

		message[11] = 0x00;
		message[12] = 0x00;
		message[13] = 0x00;
		message[14] = 0x00;
		return message;
	}

	public int getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(int sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public int getActivationType() {
		return activationType;
	}

	public void setActivationType(int activationType) {
		this.activationType = activationType;
	}

	public long getOemData() {
		return oemData;
	}

	public void setOemData(long oemData) {
		this.oemData = oemData;
	}
}
