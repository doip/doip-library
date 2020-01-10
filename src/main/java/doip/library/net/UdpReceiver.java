package doip.library.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.LinkedList;

import doip.logging.LogManager;
import doip.logging.Logger;

/**
 * Implements the publisher for listening on a UDP socket. It informs
 * its listeners when a new package had been received.
 */
public abstract class UdpReceiver {
	
	private Logger logger = LogManager.getLogger(UdpReceiver.class);

	private LinkedList<UdpReceiverListener> listeners = new LinkedList<UdpReceiverListener>();

	/**
	 * Adds a listener 
	 * @param listener
	 */
	public void addListener(UdpReceiverListener listener) {
		this.logger.trace(">>> void addListener(UdpReceiverListener listener)");
		this.listeners.add(listener);
		this.logger.trace("<<< void addListener(UdpReceiverListener listener)");
	}

	/**
	 * Removes a listener
	 * @param listener
	 */
	public void removeListener(UdpReceiverListener listener) {
		this.logger.trace(">>> void removeListener(UdpReceiverListener listener)");
		this.listeners.remove(listener);
		this.logger.trace("<<< void removeListener(UdpReceiverListener listener)");
	}
	
	/**
	 * Will be called when new data has been received
	 * @param datagramPacket
	 */
	protected void onDatagramPacketReceived(DatagramPacket datagramPacket) {
		this.logger.trace(">>> void onDatagramPacketReceived(DatagramPacket datagramPacket)");
		Iterator<UdpReceiverListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			UdpReceiverListener listener = iter.next();
			listener.onDatagramPacketReceived(this, datagramPacket);
		}
		this.logger.trace("<<< void onDatagramPacketReceived(DatagramPacket datagramPacket)");
	}
	
	/**
	 * Starts receiving data from the socket
	 * @param socket
	 */
	public abstract void start(DatagramSocket socket);
	
	/**
	 * Stops receiving data from the socket
	 */
	public abstract void stop();
}
