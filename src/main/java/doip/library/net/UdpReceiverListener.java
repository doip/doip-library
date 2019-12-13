package doip.library.net;

import java.net.DatagramPacket;

public interface UdpReceiverListener {
	public void onDatagramPacketReceived(UdpReceiver udpReceiver, DatagramPacket datagramPacket);
}
