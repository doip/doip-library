package doip.library.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import doip.logging.LogManager;
import doip.logging.Logger;

import doip.library.util.Conversion;
import doip.library.util.Helper;

/**
 * Implements a thread that listens on a UDP socket for new data which has been received. 
 */
public class UdpReceiverThread extends UdpReceiver implements Runnable {

	private Logger logger = LogManager.getLogger(UdpReceiverThread.class);

	private volatile Thread thread = null;

	private String threadName = null;

	private volatile DatagramSocket socket = null;

	/**
	 * Constructor with parameter threadName
	 * @param threadName The name of the thread. It will be used for logging.
	 */
	public UdpReceiverThread(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void start(DatagramSocket socket) {
		if (logger.isTraceEnabled()) {
			this.logger.trace(">>> void start(DatagramSocket socket)");
		}
		this.socket = socket;
		this.thread = new Thread(this, this.threadName);
		this.thread.start();
		if (logger.isTraceEnabled()) {
			this.logger.trace("<<< void start(DatagramSocket socket)");
		}
	}

	@Override
	public void stop() {
		if (logger.isTraceEnabled()) {
			this.logger.trace(">>> void stop()");
		}
		
		this.socket.close();
		
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error(Helper.getExceptionAsString(e));
			}
		}
		
		this.thread = null;
	
		if (logger.isTraceEnabled()) {
			this.logger.trace("<<< void stop()");
		}
	}

	/**
	 * Returns true when the thread is alive
	 * @return
	 */
	public boolean isAlive() {
		this.logger.trace(">>> boolean isAlive()");
		if (this.thread == null)
			return false;
		boolean isAlive = this.thread.isAlive();
		this.logger.trace("<<< boolean isAlive()");
		return isAlive;
	}

	@Override
	public void run() {
		if (logger.isTraceEnabled()) {
			this.logger.trace(">>> void run()");
		}
		try {
			byte[] data = new byte[0x10000];
			DatagramPacket datagramPacket = new DatagramPacket(data, 0x10000);
			for (;;) {
				this.socket.receive(datagramPacket);
				if (logger.isInfoEnabled()) {
					byte[] receivedData = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
					logger.info("UDP-RECV: Remote = " + datagramPacket.getAddress().getHostAddress() + ":"
							+ datagramPacket.getPort() + ", Data = " + Conversion.byteArrayToHexString(receivedData));
				}
				this.onDatagramPacketReceived(datagramPacket);
			}
		} catch (IOException e) {
			if (logger.isInfoEnabled()) {
				logger.info(Helper.getExceptionAsString(e));
			}
		}
		if (logger.isTraceEnabled()) {
			this.logger.trace("<<< void run()");
		}
	}
}
