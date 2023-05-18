package doip.library.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.library.exception.HeaderTooShort;
import doip.library.exception.IncorrectPatternFormat;
import doip.library.exception.InvalidPayloadLength;
import doip.library.exception.InvalidPayloadType;
import doip.library.message.DoipHeaderNegAck;
import doip.library.message.DoipMessage;
import doip.library.message.DoipUdpDiagnosticPowerModeRequest;
import doip.library.message.DoipUdpDiagnosticPowerModeResponse;
import doip.library.message.DoipUdpEntityStatusRequest;
import doip.library.message.DoipUdpEntityStatusResponse;
import doip.library.message.DoipUdpHeaderNegAck;
import doip.library.message.DoipUdpMessage;
import doip.library.message.DoipUdpVehicleAnnouncementMessage;
import doip.library.message.DoipUdpVehicleIdentRequest;
import doip.library.message.DoipUdpVehicleIdentRequestWithEid;
import doip.library.message.DoipUdpVehicleIdentRequestWithVin;
import doip.library.net.UdpReceiver;
import doip.library.net.UdpReceiverListener;
import doip.library.net.UdpReceiverThread;
import doip.library.util.Conversion;
import doip.library.util.Helper;
import doip.library.util.LookupTable;

public class DoipUdpMessageHandler implements UdpReceiverListener {

	private static Logger logger = LogManager.getLogger(DoipUdpMessageHandler.class);

	private DatagramSocket socket = null;

	private String udpReceiverThreadName = null;

	private UdpReceiverThread udpReceiverThread = null;

	private LinkedList<DoipUdpMessageHandlerListener> listeners = new LinkedList<DoipUdpMessageHandlerListener>();

	private LookupTable lookupTable = null;

	public DoipUdpMessageHandler(String udpReceiverThreadName, LookupTable lookupTable) {
		this.udpReceiverThreadName = udpReceiverThreadName;
		this.lookupTable = lookupTable;
	}

	public void addListener(DoipUdpMessageHandlerListener listener) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void addListener(DoipUdpInterpreterListener listener)");
		}

		this.listeners.add(listener);

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void addListener(DoipUdpInterpreterListener listener)");
		}
	}

	public void removeListener(DoipUdpMessageHandlerListener listener) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void removeListener(DoipUdpInterpreterListener listener)");
		}
		this.listeners.remove(listener);
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void removeListener(DoipUdpInterpreterListener listener)");
		}
	}

	public void start(DatagramSocket socket) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void start(DatagramSocket socket)");
		}

		this.socket = socket;
		this.udpReceiverThread = new UdpReceiverThread(udpReceiverThreadName);
		this.udpReceiverThread.addListener(this);
		this.udpReceiverThread.start(this.socket);

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void start(DatagramSocket socket)");
		}
	}

	public void stop() {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void stop()");
		}

		this.udpReceiverThread.stop();
		this.udpReceiverThread.removeListener(this);
		this.udpReceiverThread = null;

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void stop()");
		}
	}
	
	public void send(DoipMessage doipMessage, InetAddress target, int port) throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void send(DoipMessage doipMessage, InetAddress target, int port)");
		}
		
		byte[] message = doipMessage.getMessage();
		this.sendDatagramPacket(message, message.length, target, port);

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void send(DoipMessage doipMessage, InetAddress target, int port)");
		}
	}

	public void sendDatagramPacket(byte[] data, int length, InetAddress target, int port) throws IOException {

		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void sendDatagramPacket(byte[] data, int length, InetAddress target, int port)");
		}

	
		DatagramPacket packet = new DatagramPacket(data, length, target, port);
		
		try {
			
			logger.info("UDP-SEND: Target = " + target.getHostAddress() + ":" + port + ", Data = "
					+ Conversion.byteArrayToHexString(data));
			
			this.socket.send(packet);
			
		} catch (IOException e) {

			logger.error(Helper.getExceptionAsString(e));

			if (logger.isTraceEnabled()) {
				logger.trace("<<< void sendDatagramPacket(byte[] data, int length, InetAddress target, int port) "
						+ "return with IOException");
			}

			throw e;
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void sendDatagramPacket(byte[] data, int length, InetAddress target, int port)");
		}
	}

	@Override
	public void onDatagramPacketReceived(UdpReceiver udpReceiver, DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void onDatagramPacketReceived(UdpReceiver udpReceiver, DatagramPacket packet)");
		}

		boolean ret = false;

		ret = processDatagramByFunction(packet);
		if (ret) {
			logger.debug(
					"UDP message had been handled by 'processDatagramByFunction(DatagramPacket packet)', no further processing required.");
			if (logger.isTraceEnabled()) {
				logger.trace(
						"<<< public void onDatagramPacketReceived(UdpReceiver udpReceiver, DatagramPacket packet)");
			}
			return;
		}

		ret = processDatagramByLookupTable(packet);
		if (ret) {
			logger.debug(
					"UDP message had been handled by 'processDatagramByLookupTable(DatagramPacket packet)', no further processing required.");
			if (logger.isTraceEnabled()) {
				logger.trace(
						"<<< public void onDatagramPacketReceived(UdpReceiver udpReceiver, DatagramPacket packet)");
			}
			return;
		}

		processDatagramByMessageInterpretation(packet);
		logger.debug("UDP message had been handled by 'processDatagramByMessageInterpretation(DatagramPacket packet)'");
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void onDatagramPacketReceived(UdpReceiver udpReceiver, DatagramPacket packet)");
		}
	}

	/**
	 * Checks the UDP lookup table if there is a pattern which matches to the given
	 * UDP message. If it could be found then this UDP message will be send back to
	 * the sender. If no response could be found the function returns false.
	 * 
	 * @param packet The incoming datagram message
	 * @return Returns true if the datagram had been handled.
	 */

	public boolean processDatagramByLookupTable(DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public boolean processDatagramByLookupTable(DatagramPacket packet)");
		}
		boolean ret = false;
		byte[] response = null;

		// If a lookup table exists search for a response
		if (lookupTable != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Search request in UDP lookup table");
			}
			byte[] request = Arrays.copyOf(packet.getData(), packet.getLength());
			response = lookupTable.findResultAndApplyModifiers(request);
		}

		// Check if response had been found
		if (response != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Found response, data = " + Conversion.byteArrayToHexString(response));
			}
			try {
				this.sendDatagramPacket(response, response.length, packet.getAddress(), packet.getPort());
				ret = true;
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error(Helper.getExceptionAsString(e));
				}
			}
		} else {
			logger.debug("No response found");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public boolean processDatagramByLookupTable(DatagramPacket packet) = " + ret);
		}
		return ret;
	}

	public boolean processDatagramByFunction(DatagramPacket packet) {
		return false;
	}

	public void processDatagramByMessageInterpretation(DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public boolean processDatagramByMessageInterpretation(DatagramPacket packet)");
		}

		byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
		DoipUdpMessage doipUdpMessage = null;
		try {
			doipUdpMessage = DoipUdpMessage.parseUDP(data);
		} catch (HeaderTooShort e) {
			logger.warn(Helper.getExceptionAsString(e));
			this.onHeaderTooShort(packet);

		} catch (IncorrectPatternFormat e) {
			logger.warn(Helper.getExceptionAsString(e));
			this.onHeaderIncorrectPatternFormat(packet);

		} catch (InvalidPayloadLength e) {
			logger.warn(Helper.getExceptionAsString(e));
			this.onInvalidPayloadLength(packet);

		} catch (InvalidPayloadType e) {
			logger.warn(Helper.getExceptionAsString(e));
			this.onInvalidPayloadType(packet);
		}

		if (doipUdpMessage != null) {
			if (doipUdpMessage instanceof DoipUdpVehicleIdentRequest) {
				this.onDoipUdpVehicleIdentRequest((DoipUdpVehicleIdentRequest) doipUdpMessage, packet);
			} else if (doipUdpMessage instanceof DoipUdpVehicleIdentRequestWithEid) {
				this.onDoipUdpVehicleIdentRequestWithEid((DoipUdpVehicleIdentRequestWithEid) doipUdpMessage, packet);
			} else if (doipUdpMessage instanceof DoipUdpVehicleIdentRequestWithVin) {
				this.onDoipUdpVehicleIdentRequestWithVin((DoipUdpVehicleIdentRequestWithVin) doipUdpMessage, packet);
			} else if (doipUdpMessage instanceof DoipUdpVehicleAnnouncementMessage) {
				this.onDoipUdpVehicleAnnouncementMessage((DoipUdpVehicleAnnouncementMessage) doipUdpMessage, packet);
			} else if (doipUdpMessage instanceof DoipUdpDiagnosticPowerModeRequest) {
				this.onDoipUdpDiagnosticPowerModeRequest((DoipUdpDiagnosticPowerModeRequest) doipUdpMessage, packet);
			} else if (doipUdpMessage instanceof DoipUdpDiagnosticPowerModeResponse) {
				this.onDoipUdpDiagnosticPowerModeResponse((DoipUdpDiagnosticPowerModeResponse) doipUdpMessage, packet);
			} else if (doipUdpMessage instanceof DoipUdpEntityStatusRequest) {
				this.onDoipUdpEntityStatusRequest((DoipUdpEntityStatusRequest) doipUdpMessage, packet);
			} else if (doipUdpMessage instanceof DoipUdpEntityStatusResponse) {
				this.onDoipUdpEntityStatusResponse((DoipUdpEntityStatusResponse) doipUdpMessage, packet);
			} else if (doipUdpMessage instanceof DoipHeaderNegAck) {
				this.onDoipUdpHeaderNegAck((DoipUdpHeaderNegAck) doipUdpMessage, packet);
			} else {
				logger.fatal("Unhandled UDP message which is instance of " + doipUdpMessage.getClass().getName());
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public boolean processDatagramByMessageInterpretation(DatagramPacket packet)");
		}
	}

	public void onDoipUdpVehicleIdentRequest(DoipUdpVehicleIdentRequest doipMessage, DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpVehicleIdentRequest(DoipUdpVehicleIdentRequest doipMessage, DatagramPacket packet)");
		}

		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			listener.onDoipUdpVehicleIdentRequest(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpVehicleIdentRequest(DoipUdpVehicleIdentRequest doipMessage, DatagramPacket packet)");
		}
	}

	public void onDoipUdpVehicleIdentRequestWithEid(DoipUdpVehicleIdentRequestWithEid doipMessage, DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpVehicleIdentRequestWithEid(DoipUdpVehicleIdentRequestWithEid doipMessage, DatagramPacket packet)");
		}

		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			listener.onDoipUdpVehicleIdentRequestWithEid(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpVehicleIdentRequestWithEid(DoipUdpVehicleIdentRequestWithEid doipMessage, DatagramPacket packet)");
		}
	}

	public void onDoipUdpVehicleIdentRequestWithVin(DoipUdpVehicleIdentRequestWithVin doipMessage, DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpVehicleIdentRequestWithVin(DoipUdpVehicleIdentRequestWithVin doipMessage, DatagramPacket packet)");
		}

		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			listener.onDoipUdpVehicleIdentRequestWithVin(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpVehicleIdentRequestWithVin(DoipUdpVehicleIdentRequestWithVin doipMessage, DatagramPacket packet)");
		}
	}

	public void onDoipUdpVehicleAnnouncementMessage(DoipUdpVehicleAnnouncementMessage doipMessage,
			DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpVehicleAnnouncementMessage(DoipUdpVehicleAnnouncementMessage doipMessage, DatagramPacket packet)");
		}

		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			listener.onDoipUdpVehicleAnnouncementMessage(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpVehicleAnnouncementMessage(DoipUdpVehicleAnnouncementMessage doipMessage, DatagramPacket packet)");
		}
	}

	public void onDoipUdpDiagnosticPowerModeRequest(DoipUdpDiagnosticPowerModeRequest doipMessage,
			DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpDiagnosticPowerModeRequest(DoipUdpDiagnosticPowerModeRequest doipMessage, DatagramPacket packet)");
		}

		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			listener.onDoipUdpDiagnosticPowerModeRequest(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpDiagnosticPowerModeRequest(DoipUdpDiagnosticPowerModeRequest doipMessage, DatagramPacket packet)");
		}
	}

	public void onDoipUdpDiagnosticPowerModeResponse(DoipUdpDiagnosticPowerModeResponse doipMessage,
			DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpDiagnosticPowerModeResponse(DoipUdpDiagnosticPowerModeResponse doipMessage, DatagramPacket packet)");
		}

		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			listener.onDoipUdpDiagnosticPowerModeResponse(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpDiagnosticPowerModeResponse(DoipUdpDiagnosticPowerModeResponse doipMessage, DatagramPacket packet)");
		}
	}

	public void onDoipUdpEntityStatusRequest(DoipUdpEntityStatusRequest doipMessage, DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpEntityStatusRequest(DoipUdpEntityStatusRequest doipMessage, DatagramPacket packet)");
		}

		logger.debug("Number of listeners = " + this.listeners.size());
		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			logger.debug("Calling listener on address " + listener.toString());
			listener.onDoipUdpEntityStatusRequest(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpEntityStatusRequest(DoipUdpEntityStatusRequest doipMessage, DatagramPacket packet)");
		}
	}

	public void onDoipUdpEntityStatusResponse(DoipUdpEntityStatusResponse doipMessage, DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpEntityStatusResponse(DoipUdpEntityStatusResponse doipMessage, DatagramPacket packet)");
		}

		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			listener.onDoipUdpEntityStatusResponse(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpEntityStatusResponse(DoipUdpEntityStatusResponse doipMessage, DatagramPacket packet)");
		}
	}

	public void onDoipUdpHeaderNegAck(DoipUdpHeaderNegAck doipMessage, DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(
					">>> public void onDoipUdpHeaderNegAck(DoipUdpHeaderNegAck doipMessage, DatagramPacket packet)");
		}

		Iterator<DoipUdpMessageHandlerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipUdpMessageHandlerListener listener = iter.next();
			listener.onDoipUdpHeaderNegAck(doipMessage, packet);
		}

		if (logger.isTraceEnabled()) {
			logger.trace(
					"<<< public void onDoipUdpHeaderNegAck(DoipUdpHeaderNegAck doipMessage, DatagramPacket packet)");
		}
	}

	public void onHeaderIncorrectPatternFormat(DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void onHeaderIncorrectPatternFormat(DatagramPacket packet)");
		}

		DoipUdpHeaderNegAck doipResponse = new DoipUdpHeaderNegAck(DoipUdpHeaderNegAck.NACK_INCORRECT_PATTERN_FORMAT);
		byte[] response = doipResponse.getMessage();
		InetAddress targetAddress = packet.getAddress();
		int targetPort = packet.getPort();
		try {
			this.sendDatagramPacket(response, response.length, targetAddress, targetPort);
		} catch (IOException e) {
			logger.error(Helper.getExceptionAsString(e));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void onHeaderIncorrectPatternFormat(DatagramPacket packet)");
		}
	}

	public void onHeaderTooShort(DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void onHdeaderTooShort(DatagramPacket packet)");
		}

		DoipUdpHeaderNegAck doipResponse = new DoipUdpHeaderNegAck(DoipUdpHeaderNegAck.NACK_INVALID_PAYLOAD_LENGTH);
		byte[] response = doipResponse.getMessage();
		InetAddress targetAddress = packet.getAddress();
		int targetPort = packet.getPort();
		try {
			this.sendDatagramPacket(response, response.length, targetAddress, targetPort);
		} catch (IOException e) {
			logger.error(Helper.getExceptionAsString(e));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void onHdeaderTooShort(DatagramPacket packet)");
		}
	}

	public void onInvalidPayloadLength(DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void onInvalidPayloadLength(DatagramPacket packet)");
		}

		DoipUdpHeaderNegAck doipResponse = new DoipUdpHeaderNegAck(DoipUdpHeaderNegAck.NACK_INVALID_PAYLOAD_LENGTH);
		byte[] response = doipResponse.getMessage();
		InetAddress targetAddress = packet.getAddress();
		int targetPort = packet.getPort();
		try {
			this.sendDatagramPacket(response, response.length, targetAddress, targetPort);
		} catch (IOException e) {
			logger.error(Helper.getExceptionAsString(e));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void onInvalidPayloadLength(DatagramPacket packet)");
		}
	}

	public void onInvalidPayloadType(DatagramPacket packet) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void onInvalidPayloadType(DatagramPacket packet)");
		}

		DoipUdpHeaderNegAck doipResponse = new DoipUdpHeaderNegAck(DoipUdpHeaderNegAck.NACK_UNKNOWN_PAYLOAD_TYPE);
		byte[] response = doipResponse.getMessage();
		InetAddress targetAddress = packet.getAddress();
		int targetPort = packet.getPort();
		try {
			this.sendDatagramPacket(response, response.length, targetAddress, targetPort);
		} catch (IOException e) {
			logger.error(Helper.getExceptionAsString(e));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void onInvalidPayloadType(DatagramPacket packet)");
		}
	}
}
