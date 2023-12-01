package doip.library.net;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

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
	
	private LinkedList<TcpReceiverListener> listeners = new LinkedList<TcpReceiverListener>();
	
	/**
	 * This map contains key/value pairs which are used to set the
	 * ThreadContext for Log4j. The ThreadContext will be set
	 * at the beginning of the method "run" which will be called by
	 * the JRE.
	 */
	private Map<String, String> context = null;

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
	 * Getter for the field "context"
	 * @return Returns the field "context"
	 */
	public Map<String, String> getContext() {
		return this.context;
	}
	
	/**
	 * Sets the field "context"
	 *
	 * @param  context  A map containing key-value pairs representing the context
	 */
	public void setContext(Map<String, String> context) {
		this.context = context;
	}
	
	/**
	 * Will be called when data has been received from the TCP socket.
	 * It informs all listeners that new data has been received.
	 * @param data The data which had been received
	 */
	public void onDataReceived(byte[] data) {
		logger.trace(enter, ">>> void onDataReceived(byte[] data)");
		for (TcpReceiverListener listener : this.listeners) {
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
