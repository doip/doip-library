package doip.library.net;

public interface TcpReceiverListener {
	public void onDataReceived(byte[] data);
	public void onSocketClosed();
}
