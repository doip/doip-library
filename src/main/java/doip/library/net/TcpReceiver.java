package doip.library.net;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpReceiver {
	
	private Logger logger = LogManager.getLogger(TcpReceiver.class);
	
	LinkedList<TcpReceiverListener> listeners = new LinkedList<TcpReceiverListener>();

	public void addListener(TcpReceiverListener listener) {
		this.logger.trace(">>> void addListener(TcpReceiverListener listener)");
		this.listeners.add(listener);
		this.logger.trace("<<< void addListener(TcpReceiverListener listener)");
	}

	public void removeListener(TcpReceiverListener listener) {
		this.logger.trace(">>> void removeListener(TcpReceiverListener listener)");
		this.listeners.remove(listener);
		this.logger.trace("<<< void removeListener(TcpReceiverListener listener)");
	}
	
	public void onDataReceived(byte[] data) {
		this.logger.trace(">>> void onDataReceived(byte[] data)");
		Iterator<TcpReceiverListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			TcpReceiverListener listener = iter.next();
			listener.onDataReceived(data);
		}
		this.logger.trace("<<< void onDataReceived(byte[] data)");
	}
	
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
