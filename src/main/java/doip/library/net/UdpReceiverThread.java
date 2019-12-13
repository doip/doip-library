package doip.library.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.library.util.Conversion;
import doip.library.util.Helper;

public class UdpReceiverThread extends UdpReceiver implements Runnable {

	private Logger logger = LogManager.getLogger(UdpReceiverThread.class);

	private volatile Thread thread = null;

	private String threadName = null;

	private volatile DatagramSocket socket = null;

	@SuppressWarnings("unused")
	private UdpReceiverThread() {
	}

	public UdpReceiverThread(String threadName) {
		this.threadName = threadName;
	}

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
