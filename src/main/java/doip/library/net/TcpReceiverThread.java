package doip.library.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

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

	/** Log4j logger */
	private static Logger logger = LogManager.getLogger(TcpReceiverThread.class);

	/** Log4j marker for function entry */
	private static Marker enter = MarkerManager.getMarker("ENTER");
	
	/** Log4j marker for function exit */
	private static Marker exit = MarkerManager.getMarker("EXIT'");
	
	/** This is the thread which will be started. */
	private volatile Thread thread = null;

	/**
	 * Name of the thread. This can be any name. It will be used in Log4j
	 * when the thread name will be logged in the log file.
	 */
	private String threadName = null;

	/** The socket on which the thread will listen for incoming data. */
	private volatile Socket socket = null;

	/** Maximum number of bytes which will be logged for messages. */
	private int maxByteArraySizeLogging = 64;

	/**
	 * Constructor with the thread name.
	 * 
	 * @param threadName Thread name which will be logged in the log file.
	 * @param maxByteArraySizeLogging @see maxByteArraySizeLogging
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
		logger.trace(">>> boolean isAlive()");
		if (this.thread == null)
			return false;
		boolean isAlive = this.thread.isAlive();
		logger.trace("<<< boolean isAlive()");
		return isAlive;
	}

	/**
	 * Starts the thread.
	 * 
	 * @param socket Socket on which the thread will listen for new incoming data.
	 */
	public void start(Socket socket) {
		logger.trace(">>> void start()");
		this.socket = socket;
		this.thread = new Thread(this, this.threadName);
		this.thread.start();
		logger.trace("<<< void start()");
	}

	/**
	 * Stops the thread. That also includes that the socket will be closed. That is
	 * the only way to terminate the blocking function call to read data from the
	 * socket.
	 */
	public void stop() {
		logger.trace(">>> void stop()");
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
		logger.trace("<<< void stop()");
	}

	/**
	 * This is the function which is running in the thread. It calls the function
	 * "read(byte[] data)" at the input stream of the socket.
	 */
	@Override
	public void run() {
		logger.trace(enter, ">>> void run()");

		try {
			byte[] data = new byte[0x10000];
			InputStream inputStream = this.socket.getInputStream();
			for (;;) {
				logger.debug("Read data from socket input stream (blocking read) ...");
				int count = inputStream.read(data);
	
				if (count <= 0) {
					if (this.socket.isConnected())
						this.socket.close();
					break;
				} else {
					byte[] receivedData = Arrays.copyOf(data, count);
				
					logger.info("TCP-RECV: Remote = " + this.socket.getInetAddress().getHostAddress() + ":"
							+ this.socket.getPort() + ", Length = " + receivedData.length + ", Data = " + Conversion
								.byteArrayToHexStringShortDotted(receivedData, this.maxByteArraySizeLogging));
				
					this.onDataReceived(receivedData);
				}
			}
			logger.debug("No more data to receive. Thread will terminate.");
			
		} catch (IOException e) {
			logger.info("An IOException occured while reading data from the socket.");
			logger.info("This might be because");
			logger.info("    - the TCP connection has been closed by the remote host,");
			logger.info("    - the connection has been closed by the local host or");
			logger.info("    - a TCP communication error did occur.");
			logger.info(Helper.getExceptionAsString(e));
		}
		this.onSocketClosed();
		logger.trace(exit, "<<< void run()");
	}

	public Socket getSocket() {
		return socket;
	}

}
