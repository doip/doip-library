package doip.library.net;

/**
 * Interface of a listener which listens on events from the class TcpReceiver.
 */
public interface TcpReceiverListener {
	
	/**
	 * Will be called when data has been received on the TCP socket
	 * @param data The data which had been received on the TCP socket
	 */
	public void onDataReceived(byte[] data);
	
	/**
	 * Will be called when the TCP socket had been closed
	 */
	public void onSocketClosed();
}
