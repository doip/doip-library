package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
	private static Logger logger = LogManager.getLogger(DoipMessage.class);

	protected DoipMessage() {
	}

	public abstract byte[] getMessage();

	public abstract void log(Level level);
	
	public abstract String getMessageName();
	
	public static String getPayloadTypeAsString(int type) { 
		switch (type) {
		case 0x0000:
			return "generic DoIP header negative acknowledge";
		case 0x0001:
			return "vehicle identification request message";
		case 0x0002:
			return "vehicle identification request message with EID";
		case 0x0003:
			return "vehicle identification request message with VIN";
		case 0x0004:
			return "vehicle announcement message";
		case 0x0005:
			return "routing activation request";
		case 0x0006:
			return "routing activation response";
		case 0x0007:
			return "alive check request";
		case 0x0008:
			return "alive check response";
		case 0x4001:
			return "DoIP entity status request";
		case 0x4002:
			return "DoIP entity status response";
		case 0x4003:
			return "diagnostic power mode information request";
		case 0x4004:
			return "diagnostic power mode information response";
		case 0x8001:
			return "diagnostic message";
		case 0x8002:
			return "diagnostic message positive acknowledgement";
		case 0x8003:
			return "diagnostic message negative acknowledgement";
		default:
			throw logger.throwing(Level.FATAL, new IllegalArgumentException("An invaid value has been passed to function DoipMessage.getPayoadTypeAsString(int type)"));
		}
	}
}
