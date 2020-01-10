package doip.library.net;

import java.util.Iterator;
import java.util.LinkedList;

import doip.logging.LogManager;
import doip.logging.Logger;

/**
 *  Implements functions to handle events from a TCP socket.
 *  It is implemented as a typical publisher-subscriber pattern.
 *
 */
public class TcpReceiver {
	
	private Logger logger = LogManager.getLogger(TcpReceiver.class);
	
	LinkedList<TcpReceiverListener> listeners = new LinkedList<TcpReceiverListener>();

	/**
	 * Adds a listener to this class
	 * @param listener The listener which shall be added
	 */
	public void addListener(TcpReceiverListener listener) {
		this.logger.trace(">>> void addListener(TcpReceiverListener listener)");
		this.listeners.add(listener);
		this.logger.trace("<<< void addListener(TcpReceiverListener listener)");
	}

	/**
	 * Removes a listener from this class
	 * @param listener The listener which shall be removed
	 */
	public void removeListener(TcpReceiverListener listener) {
		this.logger.trace(">>> void removeListener(TcpReceiverListener listener)");
		this.listeners.remove(listener);
		this.logger.trace("<<< void removeListener(TcpReceiverListener listener)");
	}
	
	/**
	 * Will be called when data has been received from the TCP socket.
	 * It informs all listeners that new data has been received.
	 * @param data The data which had been received
	 */
	public void onDataReceived(byte[] data) {
		this.logger.trace(">>> void onDataReceived(byte[] data)");
		Iterator<TcpReceiverListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			TcpReceiverListener listener = iter.next();
			listener.onDataReceived(data);
		}
		this.logger.trace("<<< void onDataReceived(byte[] data)");
	}
	
	/**
	 * Will be called when the socket will be closed. It 
	 * informs all listeners that the socket had been closed.
	 */
	public void onSocketClosed() {
		this.logger.trace(">>> public void onSocketClosed()");
		Iterator<TcpReceiverListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			TcpReceiverListener listener = iter.next();
			listener.onSocketClosed();
		}
		this.logger.trace("<<< public void onSocketClosed()");
	}
}
