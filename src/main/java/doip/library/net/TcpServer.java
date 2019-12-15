package doip.library.net;

import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import doip.logging.LogManager;
import doip.logging.Logger;

public class TcpServer {

	private Logger logger = LogManager.getLogger(TcpServer.class);
	
	private LinkedList<TcpServerListener> listeners = new LinkedList<TcpServerListener>();
	
	public void addListener(TcpServerListener listener) {
		this.logger.trace(">>> void addListener(TcpServerListener listener)");
		this.listeners.add(listener);
		this.logger.trace("<<< void addListener(TcpServerListener listener)");
	}

	public void removeListener(TcpServerListener listener) {
		this.logger.trace(">>> void removeListener(TcpServerListener listener)");
		this.listeners.remove(listener);
		this.logger.trace("<<< void removeListener(TcpServerListener listener)");
	}

	public void onConnectionAccepted(Socket connectionSocket) {
		this.logger.trace(">>> void onConnectionAccepted(Socket connectionSocket)");
		Iterator<TcpServerListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			TcpServerListener listener = iter.next();
			listener.onConnectionAccepted(this, connectionSocket);
		}
		this.logger.trace("<<< void onConnectionAccepted(Socket connectionSocket)");
	}
}
