package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipTcpRoutingActivationResponse extends DoipTcpMessage {

	private static Logger logger = LogManager.getLogger(DoipTcpRoutingActivationResponse.class);
	
	int testerAddress = 0;
	int entityAddress = 0;
	int responseCode = 0;
	long oemData = -1;
	
	private DoipTcpRoutingActivationResponse() {}

	public DoipTcpRoutingActivationResponse(int testerAddress, int entityAddress, int responseCode, long oemData) {
		this.testerAddress = testerAddress;
		this.entityAddress = entityAddress;
		this.responseCode = responseCode;
		this.oemData = oemData;
		this.log(Level.INFO);
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_ROUTING_RES);
	}

	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_TCP_ROUTING_RES);
	}
	
	public String getResponseCodeAsString(int code) {
		switch (code) {
		case 0x00:
			return "0x00 (routing activation denied due to unknown source address";
		case 0x01:
			return "0x01 (routing activation denied because all concurrently supported TCP_DATA sockets are registered and active)";
		case 0x02:
			return "0x02 (routing activation denied because an SA different from the table connection entry was received on the already activated TCP_DATA socket)";
		case 0x03:
			return "0x03 (routing activation denied because the SA is already registered and active on a different TCP_DATA socket)";
		case 0x04:
			return "0x04 (routing activation denied due to missing authentication)";
		case 0x05:
			return "0x05 (routing activation denied due to rejected confirmation)";
		case 0x06:
			return "0x06 (routing activation denied due to unsupported routing activation type)";
		case 0x10:
			return "0x10 (routing successfull activated)";
		case 0x11:
			return "0x11 (routing will be activated; confirmation required)";
		default:
			return String.format("0x02X (???)", code);
				
		}
	}
	
	
	
	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level,  "DoIP routing activation response:");
		logger.log(level,  "    Tester address = " + this.testerAddress);
		logger.log(level,  "    Entity address = " + this.entityAddress);
		logger.log(level,  "    Response code  = " + this.getResponseCodeAsString(this.responseCode));
		if (this.oemData == -1) {
			logger.log(level,  "    OEM data       = n/a");
		} else {
			logger.log(level,  "    OEM data       = " + this.oemData);
		}
		logger.log(level, "----------------------------------------");
	}

	@Override
	public void parsePayload(byte[] payload) {
		int high = payload[0] & 0xFF;
		int low = payload[1] & 0xFF;
		this.testerAddress = (high << 8) | low;
		high = payload[2] & 0xFF;
		low = payload[3] & 0xFF;
		this.entityAddress = (high << 8) | low;
		this.responseCode = payload[4] & 0xFF;
		// TODO: parse oemData
	}

	public static DoipTcpRoutingActivationResponse createInstance(byte[] payload) {
		DoipTcpRoutingActivationResponse doipTcpRoutingActivationResponse = new DoipTcpRoutingActivationResponse();
		doipTcpRoutingActivationResponse.parsePayload(payload);
		doipTcpRoutingActivationResponse.log(Level.INFO);
		return doipTcpRoutingActivationResponse;
	}

	@Override
	public byte[] getMessage() {
		byte[] msg = null;
		if (oemData == -1) {
			msg = new byte[8 + 9];
			msg[7] = 9;
		} else {
			msg = new byte[8 + 13];
			msg[7] = 13;
			msg[17] = (byte) (this.oemData >> 24);
			msg[18] = (byte) (this.oemData >> 16);
			msg[19] = (byte) (this.oemData >> 8);
			msg[20] = (byte) (this.oemData);
		}
		msg[0] = 0x03;
		msg[1] = (byte) 0xFC;
		msg[2] = 0x00;
		msg[3] = 0x06;
		msg[4] = 0x00;
		msg[5] = 0x00;
		msg[6] = 0x00;
		// msg[7] already set above

		msg[8] = (byte) (this.testerAddress >> 8);
		msg[9] = (byte) (this.testerAddress);
		msg[10] = (byte) (this.entityAddress >> 8);
		msg[11] = (byte) (this.entityAddress);

		msg[12] = (byte) (this.responseCode);

		msg[13] = 0x00;
		msg[14] = 0x00;
		msg[15] = 0x00;
		msg[16] = 0x00;

		return msg;
	}

	public int getTesterAddress() {
		return testerAddress;
	}

	public void setTesterAddress(int testerAddress) {
		this.testerAddress = testerAddress;
	}

	public int getEntityAddress() {
		return entityAddress;
	}

	public void setEntityAddress(int entityAddress) {
		this.entityAddress = entityAddress;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
}
