package doip.library.comm;

import java.util.Iterator;
import java.util.LinkedList;

import doip.logging.LogManager;
import doip.logging.Logger;

import doip.library.message.DoipMessage;
import doip.library.util.StreamBuffer;

/**
 * This class implements a stream buffer for DoIP which is required for TCP
 * communication. Bytes can be added to the buffer with the method "void
 * append(byte[] newData)". Then the buffer will try to interpret the existing
 * data in the buffer. If valid and and data had been received to complete the
 * DoIP message the buffer will inform its listeners.
 * 
 * @author Marco Wehnert
 *
 */
public class DoipTcpStreamBuffer extends StreamBuffer {

	/**
	 * log4j2 logger
	 */
	private static Logger logger = LogManager.getLogger(DoipTcpStreamBuffer.class);

	public static final int STATE_HEADER_NOT_COMPLETED = 1;

	public static final int STATE_PAYLOAD_NOT_COMPLETED = 2;

	public static final int STATE_SHREDDER_NOT_COMPLETED = 3;

	/**
	 * Listeners which will be called when message had been completed.
	 */
	LinkedList<DoipTcpStreamBufferListener> listeners = new LinkedList<DoipTcpStreamBufferListener>();

	/**
	 * Defines the maximum length of the payload which can be accepted. If a TCP
	 * message will be received where the payload length given by the header is
	 * bigger than the maximum acceptable payload then the further data will
	 * discarded.
	 */
	private long maxPayloadLength = 4095;

	/**
	 * This is the payload type given by byte 2-3
	 */
	private int payloadType = -1;

	/**
	 * This is the payload length which is given from byte 4-7
	 */
	private long payloadLength = -1;

	private long numberOfRemainingBytesToShredder = 0;
	private byte[] lastHeader = null;
	private int state = STATE_HEADER_NOT_COMPLETED;

	public void addListener(DoipTcpStreamBufferListener listener) {
		logger.trace(">>> public void addListener(DoipStreamBufferListener listener)");
		this.listeners.add(listener);
		logger.trace("<<< public void addListener(DoipStreamBufferListener listener)");
	}

	/**
	 * Appends the new data to the internal buffer and starts to process the data in
	 * the buffer.
	 */
	public void append(byte[] newData) {
		logger.trace(">>> void append(byte[] newData)");

		logger.debug("Append " + newData.length + " bytes to the buffer");
		super.append(newData);
		boolean ret = this.processBuffer();
		while (ret) {
			logger.debug("After processing the buffer there are still " + this.getLength()
					+ " bytes in the buffer which needs to get processed. Calling processBuffer() again.");
			ret = this.processBuffer();
		}
		logger.trace("<<< void append(byte[] newData)");
	}

	/**
	 * Checks if the payload type is valid for a TCP message.
	 * 
	 * @param payloadType The payload type
	 * @return Returns true if the payload type is valid for a TCP message. Valid
	 *         payload types for UDP messages will also be declared as invalid
	 *         payload type.
	 */
	public boolean checkPayloadType(int payloadType) {
		if (payloadType == 0x0000 || payloadType == 0x0005 || payloadType == 0x0006 || payloadType == 0x0007
				|| payloadType == 0x0008 || payloadType == 0x8001 || payloadType == 0x8002 || payloadType == 0x8003) {
			return true;
		}
		return false;
	}

	/**
	 * This function will check if the payload length is correct.
	 * 
	 * @return Returns true if the payload is correct for the specific payload type.
	 */
	public boolean checkPayloadTypeSpecificLength() {
		logger.trace(">>> boolean checkPayloadTypeSpecificLength()");
		boolean ret = false;
		switch (this.payloadType) {
		case DoipMessage.TYPE_HEADER_NACK:
			if (this.payloadLength == 1)
				ret = true;
			break;
		case DoipMessage.TYPE_TCP_ALIVE_REQ:
			if (this.payloadLength == 0)
				ret = true;
			break;
		case DoipMessage.TYPE_TCP_ALIVE_RES:
			if (this.payloadLength == 2)
				ret = true;
			break;
		case DoipMessage.TYPE_TCP_DIAG_MESSAGE:
			if (this.payloadLength >= 4)
				ret = true;
			break;
		case DoipMessage.TYPE_TCP_DIAG_MESSAGE_POS_ACK:
			if (this.payloadLength >= 5)
				ret = true;
			break;
		case DoipMessage.TYPE_TCP_DIAG_MESSAGE_NEG_ACK:
			if (this.payloadLength >= 5)
				ret = true;
			break;
		case DoipMessage.TYPE_TCP_ROUTING_REQ:
			if (this.payloadLength == 7 || this.payloadLength == 11)
				ret = true;
			break;
		case DoipMessage.TYPE_TCP_ROUTING_RES:
			if (this.payloadLength == 9 || this.payloadLength == 13)
				ret = true;
			break;
		default:
			break;
		}

		logger.trace("<<< boolean checkPayloadTypeSpecificLength()");
		return ret;
	}

	/**
	 * Checks if the first two bytes given by the parameter "data" contain a valid
	 * sync pattern.
	 * 
	 * @param data The data which will be checked.
	 * @return Returns true if the first two bytes contain a valid sync pattern
	 */
	public boolean checkSyncPattern(byte[] data) {
		logger.trace(">>> boolean checkSyncPattern(byte[] data)");
		int protocolVersion = data[0] & 0xFF;
		int inverseProtocolVersion = data[1] & 0xFF;
		int xorProtocolVersion = protocolVersion ^ 0xFF;

		if (xorProtocolVersion != inverseProtocolVersion) {
			logger.info("Invalid sync pattern");
			logger.trace("<<< boolean checkSyncPattern(byte[] data)");
			return false;
		}

		logger.trace("<<< boolean checkSyncPattern(byte[] data)");
		return true;
	}

	public long getMaxPayloadLength() {
		return maxPayloadLength;
	}

	public int getState() {
		return this.state;
	}

	/**
	 * This function handles a TCP header according to figure 7 in section 7.1.2.
	 * This function shall be called only in state STATE_HEADER_NOT_COMPLETE.
	 */
	public void handleTcpHeader() {
		logger.trace(">>> void handleTcpHeader()");

		if (this.state != STATE_HEADER_NOT_COMPLETED) {
			throw new IllegalStateException(
					"void handleTcpHeader() had been called in illegal state STATE_HEADER_NOT_COMPLETE");
		}

		byte[] data = this.getData();

		logger.debug("Parse TCP message ...");
		logger.debug("\tMessage Length           = " + data.length);
		if (data.length < 8) {
			throw new IllegalStateException(
					"void handleTcpHeader() had been called, bute there are less than 8 bytes in the buffer");
		}

		if (this.checkSyncPattern(data) == false) {
			logger.warn("Invalid sync pattern");
			this.onHeaderIncorrectPatternFormat();
			this.clear();
			logger.trace("<<< void handleTcpHeader()");
			return;
		}

		int high = data[2] & 0xFF;
		int low = data[3] & 0xFF;
		this.payloadType = (high << 8) | low;
		String text = DoipMessage.getPayloadTypeAsString(payloadType);
		if (text == null)
			text = "???";
		logger.debug("\tPayload Type             = " + String.format("0x%04X", payloadType) + ": " + text);

		long highhigh = data[4] & 0xFF;
		long highlow = data[5] & 0xFF;
		long lowhigh = data[6] & 0xFF;
		long lowlow = data[7] & 0xFF;
		this.payloadLength = (highhigh << 24) | (highlow << 16) | (lowhigh << 8) | lowlow;
		logger.debug("\tPayload Length in Header = " + payloadLength);

		if (checkPayloadType(payloadType) == false) {
			logger.warn("Invalid payload type");
			this.onHeaderUnknownPayloadType();
			this.state = STATE_SHREDDER_NOT_COMPLETED;
			this.numberOfRemainingBytesToShredder = this.payloadLength;
			this.lastHeader = this.remove(8);
			logger.trace("<<< void handleTcpHeader()");
			return;
		}

		if (this.payloadLength > this.maxPayloadLength) {
			logger.warn("Payload length (= " + this.payloadLength + ") exceeds max payload length (= "
					+ this.maxPayloadLength + ")");
			this.onHeaderMessageTooLarge();
			this.state = STATE_SHREDDER_NOT_COMPLETED;
			this.numberOfRemainingBytesToShredder = this.payloadLength;
			this.lastHeader = this.remove(8);
			logger.trace("<<< void handleTcpHeader()");
			return;
		}

		/*
		 * logger.debug("Check if payload length exceeds max memory size ..."); if
		 * (this.payloadLength > this.maxMemorySize) { this.onHeaderOutOfMemory();
		 * this.state = STATE_SHREDDER_NOT_COMPLETED; this.numberOfBytesShreddered = 0;
		 * this.lastHeader = this.remove(8); logger.trace("<<< void handleTcpHeader()");
		 * return; }
		 */

		if (this.checkPayloadTypeSpecificLength() == false) {
			logger.warn("Invalid payload length");
			this.onHeaderInvalidPayloadLength();
			this.clear();
			logger.trace("<<< void handleTcpHeader()");
			return;
		}

		// All checks had been performed, so now there is a valid header
		// in the buffer
		logger.debug("Remove header (8 bytes)");
		this.lastHeader = this.remove(8);

		if (this.payloadLength == 0) {
			logger.info("Payload completed");
			this.onPayloadCompleted(this.payloadType, new byte[0]);
		} else {
			this.state = STATE_PAYLOAD_NOT_COMPLETED;
		}

		logger.debug("After removing header there are " + this.getLength() + " bytes in the buffer");
		logger.trace("<<< void handleTcpHeader()");
	}

	private void onHeaderIncorrectPatternFormat() {
		logger.trace(">>> private void onHeaderIncorrectPatternFormat()");
		Iterator<DoipTcpStreamBufferListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpStreamBufferListener listener = iter.next();
			listener.onHeaderIncorrectPatternFormat();
		}
		logger.trace("<<< private void onHeaderIncorrectPatternFormat()");
	}

	private void onHeaderInvalidPayloadLength() {
		logger.trace("private void onHeaderInvalidPayloadLength()");
		Iterator<DoipTcpStreamBufferListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpStreamBufferListener listener = iter.next();
			listener.onHeaderInvalidPayloadLength();
		}
		logger.trace("<<< private void onHeaderInvalidPayloadLength()");

	}

	private void onHeaderMessageTooLarge() {
		logger.trace(">>> private void onHeaderMessageTooLarge()");
		Iterator<DoipTcpStreamBufferListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpStreamBufferListener listener = iter.next();
			listener.onHeaderMessageTooLarge();
		}
		logger.trace("<<< private void onHeaderMessageTooLarge()");
	}

	private void onHeaderUnknownPayloadType() {
		logger.trace(">>> private void onHeaderUnknownPayloadType()");
		Iterator<DoipTcpStreamBufferListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpStreamBufferListener listener = iter.next();
			listener.onHeaderUnknownPayloadType();
		}
		logger.trace("private void onHeaderUnknownPayloadType()");
	}

	private void onPayloadCompleted(int payloadType, byte[] data) {
		Iterator<DoipTcpStreamBufferListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpStreamBufferListener listener = iter.next();
			listener.onPayloadCompleted(this.lastHeader, payloadType, data);
		}
	}


	/**
	 * This function will process the existing data in the buffer.
	 * 
	 * @return Returns true if buffer contains data which had not yet been
	 *         processed. In such a case the function processBuffer() needs to be
	 *         called again.
	 */
	public boolean processBuffer() {
		logger.trace(">>> boolean processBuffer()");
		boolean ret = false;
		switch (state) {
		case STATE_HEADER_NOT_COMPLETED:
			logger.debug("Process buffer in state STATE_HEADER_NOT_COMPLETED");
			ret = this.processBufferInStateHeaderNotCompleted();
			break;
		case STATE_PAYLOAD_NOT_COMPLETED:
			logger.debug("Process buffer in state STATE_PAYLOAD_NOT_COMPLETED");
			ret = this.processBufferInStatePayloadNotCompleted();
			break;
		case STATE_SHREDDER_NOT_COMPLETED:
			logger.debug("Process buffer in state STATE_SHREDDER_NOT_COMPLETED");
			ret = this.processBufferInStateShredderNotCompleted();
			break;
		default:
			throw new IllegalStateException();
		}
		logger.trace("<<< boolean processBuffer()");
		return ret;
	}

	/**
	 * Process the internal buffer in the state STATE_HEADER_NOT_COMPLETED
	 * 
	 * @return Returns true if after calling this function there is still data in
	 *         the buffer which needs to be processed.
	 */
	public boolean processBufferInStateHeaderNotCompleted() {
		logger.trace(">>> boolean processBufferInStateHeaderNotCompleted()");
		boolean ret = false;
		if (this.getLength() >= 8) {
			this.handleTcpHeader();
			if (this.getLength() > 0) {
				ret = true;
			}
		}
		logger.trace("<<< boolean processBufferInStateHeaderNotCompleted()");
		return ret;
	}

	public boolean processBufferInStatePayloadNotCompleted() {
		logger.trace(">>> boolean processBufferInStatePayloadNotCompleted()");
		boolean ret = false;
		byte[] data = this.getData();
		if (this.payloadLength == data.length) {
			this.clear();
			this.onPayloadCompleted(this.payloadType, data);
			this.state = STATE_HEADER_NOT_COMPLETED;
		} else if (this.payloadLength < data.length) {
			byte[] payload = this.remove((int) this.payloadLength);
			this.onPayloadCompleted(payloadType, payload);
			this.state = STATE_HEADER_NOT_COMPLETED;
			ret = true;
		}
		logger.trace("<<< boolean processBufferInStatePayloadNotCompleted()");
		return ret;
	}

	private void onShredderCompleted(long payloadLength) {
		Iterator<DoipTcpStreamBufferListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpStreamBufferListener listener = iter.next();
			listener.onShredderCompleted(this.payloadLength);
		}
	}

	public boolean processBufferInStateShredderNotCompleted() {
		logger.trace(">>> boolean processBufferInStateShredderNotCompleted()");

		// Get the current buffer
		byte[] data = this.getData();

		if (data.length > this.numberOfRemainingBytesToShredder) {
			this.remove((int) this.numberOfRemainingBytesToShredder);
			this.numberOfRemainingBytesToShredder = 0;
			this.state = STATE_HEADER_NOT_COMPLETED;
			this.onShredderCompleted(this.payloadLength);
			logger.trace("<<< boolean processBufferInStateShredderNotCompleted()");
			return true;
		} else if (data.length == this.numberOfRemainingBytesToShredder) {
			this.clear();
			this.numberOfRemainingBytesToShredder = 0;
			this.state = STATE_HEADER_NOT_COMPLETED;
			this.onShredderCompleted(this.payloadLength);
			logger.trace("<<< boolean processBufferInStateShredderNotCompleted()");
			return false;
		} else {
			// data.length < this.numberOfRemainingBytesToShredder
			this.clear();
			this.numberOfRemainingBytesToShredder -= data.length;
			logger.trace("<<< boolean processBufferInStateShredderNotCompleted()");
			return false;
		}
	}

	public void removeListener(DoipTcpStreamBufferListener listener) {
		logger.trace(">>> public void removeListener(DoipStreamBufferListener listener)");
		this.listeners.remove(listener);
		logger.trace("<<< public void removeListener(DoipStreamBufferListener listener)");
	}

	public void setMaxPayloadLength(long maxPayloadLength) {
		this.maxPayloadLength = maxPayloadLength;
	}
}
