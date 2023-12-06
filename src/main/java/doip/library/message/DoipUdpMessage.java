package doip.library.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.library.exception.HeaderTooShort;
import doip.library.exception.IncorrectPatternFormat;
import doip.library.exception.InvalidPayloadLength;
import doip.library.exception.InvalidPayloadType;

public abstract class DoipUdpMessage extends DoipMessage {

	private static Logger logger = LogManager.getLogger(DoipUdpMessage.class);

	public static DoipUdpMessage parseUDP(byte[] data)
			throws HeaderTooShort, IncorrectPatternFormat, InvalidPayloadLength, InvalidPayloadType {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> DoipMessage parseUDP(byte[] data)");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Parse UDP message ...");
			logger.debug("\tMessage Length           = " + data.length);
		}
		// Check header length
		if (data.length < 8) {
			logger.trace("<<< DoipMessage parseUDP(byte[] data) return with HeaderTooShort");
			throw new HeaderTooShort("DoIP UDP message too short for interpretation");
		}

		int protocolVersion = data[0] & 0xFF;
		int inverseProtocolVersion = data[1] & 0xFF;
		int xorProtocolVersion = protocolVersion ^ 0xFF;

		if (xorProtocolVersion != inverseProtocolVersion) {
			logger.trace("<<< DoipMessage parseUDP(byte[] data) return with IncorrectPatternFormat");
			String text = "The inverse protocol version (second byte of a"
					+ " DoIP message) doesn't match the protocol version"
					+ " (first byte of a DoIP message) XOR 0xFF;"
					+ " protocol version = "
					+ String.format("0x%02X", protocolVersion)
					+ ", inverse protocol version = "
					+ String.format("0x%02X", inverseProtocolVersion)
					+ ".";
			throw new IncorrectPatternFormat(text);
		}

		int high = data[2] & 0xFF;
		int low = data[3] & 0xFF;
		int payloadType = (high << 8) | low;
		String text = getPayloadTypeAsString(payloadType);
		if (text == null)
			text = "???";
		logger.debug("\tPayload Type             = " + String.format("0x%04X", payloadType) + ": " + text);

		long highhigh = data[4] & 0xFF;
		long highlow = data[5] & 0xFF;
		long lowhigh = data[6] & 0xFF;
		long lowlow = data[7] & 0xFF;
		long payloadLength = (highhigh << 24) | (highlow << 16) | (lowhigh << 8) | lowlow;

		logger.debug("\tPayload Length in Header = " + payloadLength);

		switch (payloadType) {
//---------------------------------------------------------
		case 0x0000: // Header NACK; TCP or UDP
//---------------------------------------------------------
			checkPayloadLength(1, payloadLength, data.length);
			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpHeaderNegAck(data[8] & 0xFF);

//---------------------------------------------------------
		case 0x0001: // VIR; UDP
//---------------------------------------------------------
			checkPayloadLength(0, payloadLength, data.length);
			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpVehicleIdentRequest();

//---------------------------------------------------------
		case 0x0002: // VIR with EID; UDP
//---------------------------------------------------------
			checkPayloadLength(6, payloadLength, data.length);

			// EID
			byte[] eid = new byte[6];
			System.arraycopy(data, 8, eid, 0, 6);

			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpVehicleIdentRequestWithEid(eid);

//---------------------------------------------------------
		case 0x0003: // VIR with VIN; UDP
//---------------------------------------------------------
			checkPayloadLength(17, payloadLength, data.length);

			// VIN
			byte[] vin = new byte[17];
			System.arraycopy(data, 8, vin, 0, 17);

			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpVehicleIdentRequestWithVin(vin);

//---------------------------------------------------------
		case 0x0004: // VAM; UDP
//---------------------------------------------------------
			/* The last byte could be optional, therefore we
			   use specific method to check the payload length */
			checkPayloadLength_0x0004_VAM(payloadLength, data.length);

			// VIN
			vin = new byte[17];
			System.arraycopy(data, 8, vin, 0, 17);

			// Logical Address
			high = data[25] & 0xFF;
			low = data[26] & 0xFF;
			int logicalAddress = (high << 8) + low;

			// EID
			eid = new byte[6];
			System.arraycopy(data, 27, eid, 0, 6);

			// GID
			byte[] gid = new byte[6];
			System.arraycopy(data, 33, gid, 0, 6);

			// Further Action Required
			int furtherActionRequired = data[39] & 0xFF;

			// Sync Status
			int syncStatus;
			if (payloadLength == 33) {
				syncStatus = (data[40] & 0xFF);
			} else {
				syncStatus = -1;
			}


			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpVehicleAnnouncementMessage(vin, logicalAddress, eid, gid, furtherActionRequired,
					syncStatus);

//---------------------------------------------------------
		case 0x4001: // Entity status request; UDP
//---------------------------------------------------------
			checkPayloadLength(0, payloadLength, data.length);

			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpEntityStatusRequest();

//---------------------------------------------------------
		case 0x4002: // Entity status response; UDP
//---------------------------------------------------------
			checkPayloadLength(7, payloadLength, data.length);

			// Node Type
			byte nodeType = data[8];

			// Max Number Of Sockets
			int maxNumberOfSockets = data[9] & 0xFF;

			// Current Number Of Sockets
			int currentNumberOfSockets = data[10] & 0xFF;

			// Max Data Size
			int maxDataSize = (data[11] << 24) | (data[12] << 16) | (data[13] << 8) | data[14];
			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpEntityStatusResponse(nodeType, maxNumberOfSockets, currentNumberOfSockets, maxDataSize);

//---------------------------------------------------------
		case 0x4003: // Diag power mode request; UDP
//---------------------------------------------------------
			checkPayloadLength(0, payloadLength, data.length);

			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpDiagnosticPowerModeRequest();

//---------------------------------------------------------
		case 0x4004: // Diag power mode response; UDP
//---------------------------------------------------------
			checkPayloadLength(1, payloadLength, data.length);

			byte diagPowerMode = data[8];

			logger.trace("<<< DoipMessage parseUDP(byte[] data)");
			return new DoipUdpDiagnosticPowerModeResponse(diagPowerMode);
		default:
			logger.trace("<<< DoipMessage parseUDP(byte[] data) return with InvalidPayloadType");
			text = "The payload type " + String.format("0x%02X", payloadType) + " doesn't have a valid value";
			throw new InvalidPayloadType(text);
		}
	}

	/**
	 * Checks the length of the payload.
	 * 
	 * @param expectedLength The expected length of the payload
	 * @param payloadLength  The lenght of the payload given in the DoIP header
	 * @param dataLength     The length of the DoIP message
	 * @throws InvalidPayloadLength If payload length is invalid an exception will
	 *                              be thrown.
	 */
	private static void checkPayloadLength(long expectedLength, long payloadLength, long dataLength)
			throws InvalidPayloadLength {
		try {
			logger.trace(">>> void checkPayloadLength(long expectedLength, long payloadLength, long dataLength)");
			
			if (payloadLength != expectedLength) {
				String text = "The payload length given in the DoIP header is invalid. " +
						"It should be " + expectedLength + ", but it is " + payloadLength + ".";
				logger.warn(text);
				throw new InvalidPayloadLength(text);
			}
			logger.debug("\tExpected Payload Length  = " + expectedLength);
			if (payloadLength != (dataLength - 8)) {
				String text = "The payload length given in the DoIP header does not match "
						+ "to the actual length of the payload. The payload given in the DoIP header is " + payloadLength + ", "
								+ "but the actual length of the payload is " + (dataLength - 8) + ".";
				logger.warn(text);
				throw new InvalidPayloadLength(text);
			}
			logger.debug("Payload length of DoIP message is correct.");
		} finally {
			logger.trace("<<< void checkPayloadLength(long expectedLength, long payloadLength, long dataLength)");
		}
	}
	
	private static void checkPayloadLength_0x0004_VAM(long payloadLength, long dataLength) throws InvalidPayloadLength {
		try {
			logger.trace(">>> void checkPayloadLength_0x0004_VAM(long payloadLength, long dataLength)");
			if ((payloadLength != 32) && (payloadLength != 33)) {
				String text = "The payload length given in the DoIP header of the vehicle announcement message (0x0004) is invalid. "
						+ "It should be 32 or 33, but it is " + payloadLength + ".";
				logger.warn(text);
				throw new InvalidPayloadLength(text);
			}
			
			if (payloadLength != (dataLength - 8)) {
				String text = "The payload length given in the DoIP header of the vehicle announcement message (0x0004) does not match "
						+ "to the actual length of the payload. The payload given in the DoIP header is " + payloadLength + ", "
								+ "but the actual length of the payload is " + (dataLength - 8) + ".";
				logger.warn(text);
				throw new InvalidPayloadLength(text);
			}
			logger.debug("Payload length of the vehicle announcement message (0x0004) is correct.");
		} finally {
			logger.trace("<<< void checkPayloadLength_0x0004_VAM(long payloadLength, long dataLength)");
		}
	}
}
