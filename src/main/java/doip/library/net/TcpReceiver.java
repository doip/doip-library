package doip.library.net;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;


/**
 *  Implements functions to handle events from a TCP socket.
 *  It is implemented as a typical publisher-subscriber pattern.
 */
public class TcpReceiver {
	
	private static Logger logger = LogManager.getLogger(TcpReceiver.class);
	private static Marker enter = MarkerManager.getMarker("ENTER");
	private static Marker exit = MarkerManager.getMarker("EXIT");
	
	LinkedList<TcpReceiverListener> listeners = new LinkedList<TcpReceiverListener>();

	/**
	 * Adds a listener to this class
	 * @param listener The listener which shall be added
	 */
	public void addListener(TcpReceiverListener listener) {
		logger.trace(enter, ">>> void addListener(TcpReceiverListener listener)");
		this.listeners.add(listener);
		logger.trace(exit, "<<< void addListener(TcpReceiverListener listener)");
	}

	/**
	 * Removes a listener from this class
	 * @param listener The listener which shall be removed
	 */
	public void removeListener(TcpReceiverListener listener) {
		logger.trace(enter, ">>> void removeListener(TcpReceiverListener listener)");
		this.listeners.remove(listener);
		logger.trace(exit, "<<< void removeListener(TcpReceiverListener listener)");
	}
	
	/**
	 * Will be called when data has been received from the TCP socket.
	 * It informs all listeners that new data has been received.
	 * @param data The data which had been received
	 */
	public void onDataReceived(byte[] data) {
		logger.trace(enter, ">>> void onDataReceived(byte[] data)");
		Iterator<TcpReceiverListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			TcpReceiverListener listener = iter.next();
			listener.onDataReceived(data);
		}
		logger.trace(exit, "<<< void onDataReceived(byte[] data)");
	}
	
	/**
	 * Will be called when the socket will be closed. It 
	 * informs all listeners that the socket had been closed.
	 */
	public void onSocketClosed() {
		logger.trace(enter, ">>> public void onSocketClosed()");
		Iterator<TcpReceiverListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			TcpReceiverListener listener = iter.next();
			listener.onSocketClosed();
		}
		logger.trace(exit, "<<< public void onSocketClosed()");
	}
}
