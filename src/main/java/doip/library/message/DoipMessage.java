package doip.library.message;

import org.apache.logging.log4j.Level;

/**
 * Base class for all DoIP Messages
 * 
 * @author Marco Wehnert
 *
 */
public abstract class DoipMessage {

	public final static int TYPE_HEADER_NACK = 0x0000;
	public final static int TYPE_UDP_VIR = 0x0001;
	public final static int TYPE_UDP_VIR_EID = 0x0002;
	public final static int TYPE_UDP_VIR_VIN = 0x0003;
	public final static int TYPE_UDP_VAM = 0x0004;
	public final static int TYPE_TCP_ROUTING_REQ = 0x0005;
	public final static int TYPE_TCP_ROUTING_RES = 0x0006;
	public final static int TYPE_TCP_ALIVE_REQ = 0x0007;
	public final static int TYPE_TCP_ALIVE_RES = 0x0008;

	public final static int TYPE_UDP_ENTITY_STATUS_REQ = 0x4001;
	public final static int TYPE_UDP_ENTITY_STATUS_RES = 0x4002;
	public final static int TYPE_UDP_DIAG_POWER_MODE_REQ = 0x4003;
	public final static int TYPE_UDP_DIAG_POWER_MODE_RES = 0x4004;

	public final static int TYPE_TCP_DIAG_MESSAGE = 0x8001;
	public final static int TYPE_TCP_DIAG_MESSAGE_POS_ACK = 0x8002;
	public final static int TYPE_TCP_DIAG_MESSAGE_NEG_ACK = 0x8003;

	protected DoipMessage() {
	}

	public abstract byte[] getMessage();

	public abstract void log(Level level);
	
	public static String getPayloadTypeAsString(int type) {
		switch (type) {
		case 0x0000:
			return "Generic DoIP Header Negative Acknowledge";
		case 0x0001:
			return "Vehicle Identification Request Message";
		case 0x0002:
			return "Vehicle Identification Request Message with EID";
		case 0x0003:
			return "Vehicle Identification Request Message with VIN";
		case 0x0004:
			return "Vehicle Announcement Message";
		case 0x0005:
			return "Routing Activation Request";
		case 0x0006:
			return "Routing Activation Response";
		case 0x0007:
			return "Alive Check Request";
		case 0x0008:
			return "Alive Check Response";
		case 0x4001:
			return "DoIP Entity Status Request";
		case 0x4002:
			return "DoIP Entity Status Response";
		case 0x4003:
			return "Diagnostic Power Mode Information Request";
		case 0x4004:
			return "Diagnostic Power Mode Information Response";
		case 0x8001:
			return "Diagnostic Message";
		case 0x8002:
			return "Diagnostic Message Positive Acknowledgement";
		case 0x8003:
			return "Diagnostic Message Negative Acknowledgement";
		default:
			break;
		}
		return null;
	}
}
