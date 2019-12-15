package doip.library.net;

import java.net.DatagramPacket;
import java.util.Iterator;
import java.util.LinkedList;

import doip.logging.LogManager;
import doip.logging.Logger;

public class UdpReceiver {
	
	private Logger logger = LogManager.getLogger(UdpReceiver.class);

	private LinkedList<UdpReceiverListener> listeners = new LinkedList<UdpReceiverListener>();

	public void addListener(UdpReceiverListener listener) {
		this.logger.trace(">>> void addListener(UdpReceiverListener listener)");
		this.listeners.add(listener);
		this.logger.trace("<<< void addListener(UdpReceiverListener listener)");
	}

	public void removeListener(UdpReceiverListener listener) {
		this.logger.trace(">>> void removeListener(UdpReceiverListener listener)");
		this.listeners.remove(listener);
		this.logger.trace("<<< void removeListener(UdpReceiverListener listener)");
	}
	
	protected void onDatagramPacketReceived(DatagramPacket datagramPacket) {
		this.logger.trace(">>> void onDatagramPacketReceived(DatagramPacket datagramPacket)");
		Iterator<UdpReceiverListener> iter = this.listeners.iterator();
		while (iter.hasNext()) {
			UdpReceiverListener listener = iter.next();
			listener.onDatagramPacketReceived(this, datagramPacket);
		}
		this.logger.trace("<<< void onDatagramPacketReceived(DatagramPacket datagramPacket)");
	}
}
