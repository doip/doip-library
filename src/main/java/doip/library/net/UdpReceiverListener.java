package doip.library.net;

import java.net.DatagramPacket;

/**
 * Interface of a listener for a UdpReceiver
 */
public interface UdpReceiverListener {
	
	/**
	 * Will be called when a new datagram packet has been received.
	 * @param udpReceiver The receiver who has received the datagram
	 * @param datagramPacket The datagram which had been received.
	 */
	public void onDatagramPacketReceived(UdpReceiver udpReceiver, DatagramPacket datagramPacket);
	
}
