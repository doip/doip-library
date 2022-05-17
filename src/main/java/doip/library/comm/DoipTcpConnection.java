package doip.library.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import doip.logging.LogManager;
import doip.logging.Logger;

import doip.library.message.DoipMessage;
import doip.library.message.DoipTcpAliveCheckRequest;
import doip.library.message.DoipTcpAliveCheckResponse;
import doip.library.message.DoipTcpDiagnosticMessage;
import doip.library.message.DoipTcpDiagnosticMessageNegAck;
import doip.library.message.DoipTcpDiagnosticMessagePosAck;
import doip.library.message.DoipTcpHeaderNegAck;
import doip.library.message.DoipTcpRoutingActivationRequest;
import doip.library.message.DoipTcpRoutingActivationResponse;
import doip.library.net.TcpReceiverListener;
import doip.library.net.TcpReceiverThread;
import doip.library.util.Conversion;
import doip.library.util.Helper;

/**
 * Implements features for a TCP connection. It can be used in a DoIP
 * simulation as well as in a DoIP tester. 
 * 
 * But be aware that the listeners functions which will be called in case
 * of a invalid message will send a DoIP negative acknowledgement message.
 * But a diagnostic tester should not send a negative acknowledgement
 * message to avoid "ping-pong" behavior. If this class is used in a 
 * diagnostic tester you should override these methods and just do nothing
 * in the function.   
 * 
 * This class contains two important
 * members: A instance of a DoipStreamBuffer and an instance of a
 * TcpReceiverThread. When the function "start(Socket socket)" will be called
 * this class creates a new TcpReceiverThread which is listening for incoming
 * data on the socket. Therefore this class will add itself to the listeners of
 * the TcpReceiverThread. When new data had been received it will append the
 * data to the instance of the DoipStreamBuffer. The DoipStreamBuffer will try
 * to interpret the data and calls its listeners when data had been processed.
 * Therefore this class adds itself also to the listeners of the
 * DoipStreamBuffer.
 * 
 * @author Marco Wehnert
 *
 */
public class DoipTcpConnection implements DoipTcpStreamBufferListener, TcpReceiverListener {

	/**
	 * log4j logger
	 */
	private static Logger logger = LogManager.getLogger(DoipTcpConnection.class);

	/**
	 * Stream buffer where received TCP data will be appended and then get
	 * processed.
	 */
	private DoipTcpStreamBuffer streamBuffer = null;

	/**
	 * This is the TCP receiver thread which will waiting for new incoming data of
	 * the TCP socket.
	 */
	private TcpReceiverThread tcpReceiverThread = null;

	/**
	 * The socket on which data will be received
	 */
	private Socket socket = null;
	
	/**
	 * Defines the maximum number (N) of bytes which will be printed from a byte field.
	 * If the byte field is longer then only the first N bytes will be printed
	 * followed by three dots.  
	 */
	private int maxByteArraySizeLogging = 64;

	/**
	 * List of listeners which will be notified when new DoIP messages had been
	 * received. A typical listener is a gateway. The gateway will handle the
	 * messages.
	 */
	private LinkedList<DoipTcpConnectionListener> listeners = new LinkedList<DoipTcpConnectionListener>();

	public DoipTcpConnection(String tcpReceiverThreadName, int maxByteArraySizeLogging) {
		this.maxByteArraySizeLogging = maxByteArraySizeLogging;
		this.streamBuffer = new DoipTcpStreamBuffer();
		this.tcpReceiverThread = new TcpReceiverThread(tcpReceiverThreadName, maxByteArraySizeLogging);
		streamBuffer.addListener(this);
	}

	/**
	 * Starts to listen on the TCP socket for incoming data.
	 * 
	 * @param socket
	 * @throws IOException 
	 */
	public void start(Socket socket) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void start(Socket socket)");
		}
		this.socket = socket;
		this.tcpReceiverThread.addListener(this);
		this.tcpReceiverThread.start(socket);
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void start(Socket socket)");
		}
	}

	/**
	 * Stops to listen on the socket for incoming data.
	 */
	public void stop() {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void stop()");
		}
		this.tcpReceiverThread.stop();
		
		/* Give the thread some time to terminate and call the listeners.
		 * The thread will call the function "onSocketClosed(...)".
		 * If it is too fast then function removeListener will remove listener
		 * before it had a chance to call the method onSocketClosed().
		 */
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		};
		this.tcpReceiverThread.removeListener(this);
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void stop()");
		}
	}

	public void send(DoipMessage doipMessage) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void send(DoipMessage message)");
		}
		byte[] messageData = doipMessage.getMessage();
		this.send(messageData);

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void send(DoipMessage message)");
		}
	}

	public void send(byte[] data) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void send(byte[] data)");
		}
		try {
			if (logger.isInfoEnabled()) {
				logger.info("TCP-SEND: Target = " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
						+ ", Length = " + data.length 
						+ ", Data = " + Conversion.byteArrayToHexStringShortDotted(data, this.maxByteArraySizeLogging));
			}
			OutputStream stream = this.socket.getOutputStream();
			stream.write(data);
			stream.flush();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error(Helper.getExceptionAsString(e));
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void send(byte[] data)");
		}
	}

	@Override
	public void onDataReceived(byte[] data) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> void onDataReceived(byte[] data)");
		}
		this.streamBuffer.append(data);
		if (logger.isTraceEnabled()) {
			logger.trace("<<< void onDataReceived(byte[] data)");
		}
	}

	@Override
	public void onHeaderIncorrectPatternFormat() {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> void onHeaderIncorrectPatternFormat()");
		}

		DoipTcpHeaderNegAck doipMessage = new DoipTcpHeaderNegAck(DoipTcpHeaderNegAck.NACK_INCORRECT_PATTERN_FORMAT);
		this.send(doipMessage);
		try {
			this.socket.close();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error(Helper.getExceptionAsString(e));
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< void onHeaderIncorrectPatternFormat()");
		}
	}

	@Override
	public void onHeaderMessageTooLarge() {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> void void onHeaderMessageTooLarge()");
		}
		DoipTcpHeaderNegAck doipMessage = new DoipTcpHeaderNegAck(DoipTcpHeaderNegAck.NACK_MESSAGE_TOO_LARGE);
		this.send(doipMessage);
		if (logger.isTraceEnabled()) {
			logger.trace("<<< void void onHeaderMessageTooLarge()");
		}
	}

	@Override
	public void onHeaderUnknownPayloadType() {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> void onHeaderUnknownPayloadType()");
		}
		DoipTcpHeaderNegAck doipMessage = new DoipTcpHeaderNegAck(DoipTcpHeaderNegAck.NACK_UNKNOWN_PAYLOAD_TYPE);
		this.send(doipMessage);
		if (logger.isTraceEnabled()) {
			logger.trace("<<< void onHeaderUnknownPayloadType()");
		}
	}

	@Override
	public void onHeaderInvalidPayloadLength() {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> void onHeaderInvalidPayloadLength()");
		}
		DoipTcpHeaderNegAck doipMessage = new DoipTcpHeaderNegAck(DoipTcpHeaderNegAck.NACK_INVALID_PAYLOAD_LENGTH);
		this.send(doipMessage);
		try {
			this.socket.close();
		} catch (IOException e) {
			logger.error(Helper.getExceptionAsString(e));
		}
		if (logger.isTraceEnabled()) {
			logger.trace("<<< void onHeaderInvalidPayloadLength()");
		}
	}

	@Override
	public void onPayloadCompleted(byte[] header, int payloadType, byte[] payload) {
		try {
			if (logger.isTraceEnabled()) {
				logger.trace(">>> void onPayloadCompleted(int payloadType, byte[] data)");
			}
			
			boolean ret = false;
			ret = processDataByCustomHandler(header, payloadType, payload);
			if (ret) {
				return;
			}
			processDataByStandardHandler(header, payloadType, payload);
	
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace("<<< void onPayloadCompleted(int payloadType, byte[] data)");
			}
		}
	}
	
	public boolean processDataByCustomHandler(byte[] header, int payloadType, byte[] payload) {
		return false;
	}

	public void processDataByStandardHandler(byte[] header, int payloadType, byte[] payload) {
		switch (payloadType) {
		case DoipMessage.TYPE_TCP_ALIVE_REQ:
			DoipTcpAliveCheckRequest doipTcpAliveCheckRequest = new DoipTcpAliveCheckRequest();
			this.processDoipTcpAliveCheckRequest(doipTcpAliveCheckRequest);
			break;
		case DoipMessage.TYPE_TCP_ALIVE_RES:
			DoipTcpAliveCheckResponse doipTcpAliveCheckResponse = DoipTcpAliveCheckResponse.createInstance(payload);
			this.processDoipTcpAliveCheckResponse(doipTcpAliveCheckResponse);
			break;
		case DoipMessage.TYPE_TCP_ROUTING_REQ:
			DoipTcpRoutingActivationRequest doipTcpRoutingActivationRequest = DoipTcpRoutingActivationRequest
					.createInstance(payload);
			this.processDoipTcpRoutingActivationRequest(doipTcpRoutingActivationRequest);
			break;
		case DoipMessage.TYPE_TCP_ROUTING_RES:
			DoipTcpRoutingActivationResponse doipTcpRoutingActivationResponse = DoipTcpRoutingActivationResponse
					.createInstance(payload);
			this.processDoipTcpRoutingActivationResponse(doipTcpRoutingActivationResponse);
			break;
		case DoipMessage.TYPE_TCP_DIAG_MESSAGE:
			DoipTcpDiagnosticMessage doipTcpDiagnosticMessage = DoipTcpDiagnosticMessage.createInstance(payload);
			this.processDoipTcpDiagnosticMessage(doipTcpDiagnosticMessage);
			break;
		case DoipMessage.TYPE_TCP_DIAG_MESSAGE_POS_ACK:
			DoipTcpDiagnosticMessagePosAck doipTcpDiagnosticMessagePosAck = DoipTcpDiagnosticMessagePosAck
					.createInstance(payload);
			this.processDoipTcpDiagnosticMessagePosAck(doipTcpDiagnosticMessagePosAck);
			break;
		case DoipMessage.TYPE_TCP_DIAG_MESSAGE_NEG_ACK:
			DoipTcpDiagnosticMessageNegAck doipTcpDiagnosticMessageNegAck = DoipTcpDiagnosticMessageNegAck
					.createInstance(payload);
			this.processDoipTcpDiagnosticMessageNegAck(doipTcpDiagnosticMessageNegAck);
			break;
		case DoipMessage.TYPE_HEADER_NACK:
			DoipTcpHeaderNegAck doipTcpHeaderNegAck = DoipTcpHeaderNegAck.createInstance(payload);
			this.processDoipTcpHeaderNegAck(doipTcpHeaderNegAck);
			break;
		default:
			logger.fatal("##############################################");
			logger.fatal("Unhandled case " + payloadType);
			logger.fatal("##############################################");
			break;
		}
	}
	

	public void processDoipTcpDiagnosticMessage(DoipTcpDiagnosticMessage doipTcpDiagnosticMessage) {
		String function = "void processDoipTcpDiagnosticMessage(DoipTcpDiagnosticMessage doipTcpDiagnosticMessage)";
		logger.trace(">>> " + function);

		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onDoipTcpDiagnosticMessage(this, doipTcpDiagnosticMessage);
		}

		logger.trace("<<< " + function);
	}

	public void processDoipTcpDiagnosticMessageNegAck(DoipTcpDiagnosticMessageNegAck doipTcpDiagnosticMessageNegAck) {
		String function = "void processDoipTcpDiagnosticMessageNegAck(DoipTcpDiagnosticMessageNegAck doipTcpDiagnosticMessageNegAck)";
		logger.trace(">>> " + function);

		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onDoipTcpDiagnosticMessageNegAck(this, doipTcpDiagnosticMessageNegAck);
		}

		logger.trace("<<< " + function);
	}

	public void processDoipTcpDiagnosticMessagePosAck(DoipTcpDiagnosticMessagePosAck doipTcpDiagnosticMessagePosAck) {
		String function = "void processDoipTcpDiagnosticMessagePosAck(DoipTcpDiagnosticMessagePosAck doipTcpDiagnosticMessagePosAck)";
		logger.trace(">>> " + function);

		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onDoipTcpDiagnosticMessagePosAck(this, doipTcpDiagnosticMessagePosAck);
		}

		logger.trace("<<< " + function);
	}

	public void processDoipTcpRoutingActivationRequest(
			DoipTcpRoutingActivationRequest doipTcpRoutingActivationRequest) {
		String function = "void processDoipTcpRoutingActivationRequest(DoipTcpRoutingActivationRequest doipTcpRoutingActivationRequest)";
		logger.trace(">>> " + function);

		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onDoipTcpRoutingActivationRequest(this, doipTcpRoutingActivationRequest);
		}

		logger.trace("<<< " + function);
	}

	public void processDoipTcpRoutingActivationResponse(
			DoipTcpRoutingActivationResponse doipTcpRoutingActivationResponse) {
		String function = "void processDoipTcpRoutingActivationResponse(DoipTcpRoutingActivationResponse doipTcpRoutingActivationResponse)";
		logger.trace(">>> " + function);

		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onDoipTcpRoutingActivationResponse(this, doipTcpRoutingActivationResponse);
		}

		logger.trace("<<< " + function);
	}

	public void processDoipTcpAliveCheckRequest(DoipTcpAliveCheckRequest doipMessage) {
		String function = "void processDoipTcpAliveCheckRequest(DoipTcpAliveCheckRequest doipMessage)";
		logger.trace(">>> " + function);

		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onDoipTcpAliveCheckRequest(this, doipMessage);
		}

		logger.trace("<<< " + function);
	}

	/**
	 * Will be called when a alive check response had been received.
	 * 
	 * @param doipTcpAliveCheckResponse
	 */
	public void processDoipTcpAliveCheckResponse(DoipTcpAliveCheckResponse doipTcpAliveCheckResponse) {
		String function = "void processDoipTcpAliveCheckResponse(DoipTcpAliveCheckResponse doipTcpAliveCheckResponse)";
		logger.trace(">>> " + function);

		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onDoipTcpAliveCheckResponse(this, doipTcpAliveCheckResponse);
		}

		logger.trace("<<< " + function);
	}

	public void processDoipTcpHeaderNegAck(DoipTcpHeaderNegAck doipTcpHeaderNegAck) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> void processDoipHeaderNegAck(DoipHeaderNegAck doipHeaderNegAck)");
		}

		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onDoipTcpHeaderNegAck(this, doipTcpHeaderNegAck);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< void processDoipHeaderNegAck(DoipHeaderNegAck doipHeaderNegAck)");
		}
	}

	@Override
	public void onSocketClosed() {
		logger.trace(">>> public void onSocketClosed()");
		logger.debug("Number of listeners: " + this.listeners.size());
		Iterator<DoipTcpConnectionListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			DoipTcpConnectionListener listener = iter.next();
			listener.onConnectionClosed(this);
		}
		logger.trace("<<< public void onSocketClosed()");
	}

	public void addListener(DoipTcpConnectionListener listener) {
		logger.trace(">>> void addListener(DoipTcpConnectionListener listener)");
		this.listeners.add(listener);
		logger.trace("<<< void addListener(DoipTcpConnectionListener listener)");
	}

	public void removeListener(DoipTcpConnectionListener listener) {
		logger.trace(">>> void removeListener(DoipTcpConnectionListener listener)");
		this.listeners.remove(listener);
		logger.trace("<<< void removeListener(DoipTcpConnectionListener listener)");
	}
	@Override
	public void onShredderCompleted(long payloadLength) {
		logger.trace(">>> public void onShredderCompleted(long payloadLength)");
		logger.trace("<<< public void onShredderCompleted(long payloadLength)");
	}

	public Socket getSocket() {
		return socket;
	}
}
