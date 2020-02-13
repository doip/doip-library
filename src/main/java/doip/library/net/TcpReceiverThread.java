package doip.library.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import doip.logging.LogManager;
import doip.logging.Logger;

import doip.library.util.Conversion;
import doip.library.util.Helper;

/**
 * This class is a thread which will listen on a socket for incoming data. When
 * new data had been received this class will inform its listeners that new data
 * had been received. This class does not create a socket. The socket must be
 * given by the function "start(Socket socket)".
 * 
 * This class does not contain any DoIP specific implementation.
 * 
 * @author Marco Wehnert
 *
 */
public class TcpReceiverThread extends TcpReceiver implements Runnable {

	/**
	 * log4j logger
	 */
	private Logger logger = LogManager.getLogger(TcpReceiverThread.class);

	/**
	 * This is the thread which will be started.
	 */
	private volatile Thread thread = null;

	/**
	 * Name of the thread. This can be any name. It will be used in log4j when the
	 * thread name will be logged in the log file.
	 */
	private String threadName = null;

	/**
	 * The socket on which the thread will listen for incoming data.
	 */
	private volatile Socket socket = null;

	/**
	 * Maximum number of bytes which will be logged.
	 */
	private int maxByteArraySizeLogging = 64;

	/**
	 * This constructor shall never be used so it had been declared private.
	 */
	@SuppressWarnings("unused")
	private TcpReceiverThread() {
	}

	/**
	 * Constructor with the thread name.
	 * 
	 * @param threadName Thread name which will be logged in the log file.
	 */
	public TcpReceiverThread(String threadName, int maxByteArraySizeLogging) {
		this.threadName = threadName;
		this.maxByteArraySizeLogging = maxByteArraySizeLogging;
	}

	/**
	 * Returns true if the thread is still running
	 * 
	 * @return
	 */
	public synchronized boolean isAlive() {
		if (logger.isTraceEnabled()) {
			this.logger.trace(">>> boolean isAlive()");
		}
		if (this.thread == null)
			return false;
		boolean isAlive = this.thread.isAlive();
		if (logger.isTraceEnabled()) {
			this.logger.trace("<<< boolean isAlive()");
		}
		return isAlive;
	}

	/**
	 * Starts the thread.
	 * 
	 * @param socket Socket on which the thread will listen for new incoming data.
	 */
	public void start(Socket socket) {
		if (logger.isTraceEnabled()) {
			this.logger.trace(">>> void start()");
		}
		this.socket = socket;
		this.thread = new Thread(this, this.threadName);
		this.thread.start();
		if (logger.isTraceEnabled()) {
			this.logger.trace("<<< void start()");
		}
	}

	/**
	 * Stops the thread. That also includes that the socket will be closed. That is
	 * the only way to terminate the blocking function call to read data from the
	 * socket.
	 */
	public void stop() {
		if (logger.isTraceEnabled()) {
			this.logger.trace(">>> void stop()");
		}
		try {
			logger.debug("Close socket");
			if (this.socket != null) {
				this.socket.close();
			}
		} catch (IOException e) {
			logger.error(Helper.getExceptionAsString(e));
		}
//		try {
//			logger.debug("Wait that thread has finished");
//			// For some reason this join blocks sometimes
//			// TODO: analyze it
//			//this.thread.join();
//		} catch (InterruptedException e) {
//			logger.error(Helper.getExceptionAsString(e));
//		}
		this.thread = null;
		if (logger.isTraceEnabled()) {
			this.logger.trace("<<< void stop()");
		}
	}

	/**
	 * This is the function which is running in the thread. It calls the function
	 * "read(byte[] data)" at the input stream of the socket.
	 */
	@Override
	public void run() {
		if (logger.isTraceEnabled()) {
			this.logger.trace(">>> void run()");
		}

		try {
			byte[] data = new byte[0x10000];
			InputStream inputStream = this.socket.getInputStream();
			for (;;) {
				if (logger.isDebugEnabled()) {
					logger.debug("Read data from socket input stream (blocking read) ...");
				}

				int count = inputStream.read(data);
	
				if (count <= 0) {
					if (this.socket.isConnected())
						this.socket.close();
					break;
				} else {
					byte[] receivedData = Arrays.copyOf(data, count);
				
					if (logger.isInfoEnabled()) {
						this.logger.info("TCP-RECV: Remote = " + this.socket.getInetAddress().getHostAddress() + ":"
								+ this.socket.getPort() + ", Length = " + receivedData.length + ", Data = " + Conversion
										.byteArrayToHexStringShortDotted(receivedData, this.maxByteArraySizeLogging));
					}
				
					this.onDataReceived(receivedData);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No more data to receive. Thread will terminate.");
			}
		} catch (IOException e) {
			logger.info(Helper.getExceptionAsString(e));
		}
		this.onSocketClosed();
		if (logger.isTraceEnabled()) {
			this.logger.trace("<<< void run()");
		}
	}

	public Socket getSocket() {
		return socket;
	}

}
