package doip.library.net;

import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class TcpServer {

	private static Logger logger = LogManager.getLogger(TcpServer.class);
	private static Marker enter = MarkerManager.getMarker("ENTER");
	private static Marker exit = MarkerManager.getMarker("EXIT");

	private LinkedList<TcpServerListener> listeners = new LinkedList<TcpServerListener>();

	/**
	 * This map contains key/value pairs which are used to set the
	 * ThreadContext for Log4j. The ThreadContext will be set
	 * at the beginning of the method "run" which will be called by
	 * the JRE.
	 */
	private Map<String, String> context = null;

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

	public void addListener(TcpServerListener listener) {
		this.logger.trace(enter, ">>> void addListener(TcpServerListener listener)");
		this.listeners.add(listener);
		this.logger.trace(exit, "<<< void addListener(TcpServerListener listener)");
	}

	public void removeListener(TcpServerListener listener) {
		this.logger.trace(enter, ">>> void removeListener(TcpServerListener listener)");
		this.listeners.remove(listener);
		this.logger.trace(exit, "<<< void removeListener(TcpServerListener listener)");
	}

	public void onConnectionAccepted(Socket connectionSocket) {
		this.logger.trace(enter, ">>> void onConnectionAccepted(Socket connectionSocket)");
		Iterator<TcpServerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			TcpServerListener listener = iter.next();
			listener.onConnectionAccepted(this, connectionSocket);
		}
		this.logger.trace(exit, "<<< void onConnectionAccepted(Socket connectionSocket)");
	}
}
