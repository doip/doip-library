package doip.library.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import doip.logging.LogManager;
import doip.logging.Logger;

import doip.library.util.Helper;

/**
 * Implements a TCP Server as a thread which is listening for 
 * incoming connections.
 */
public class TcpServerThread extends TcpServer implements Runnable {

	private static Logger logger = LogManager.getLogger(TcpServerThread.class);

	private volatile Thread thread = null;

	private String threadName = null;

	private volatile ServerSocket socket = null;

	public TcpServerThread(String threadName) {
		this.threadName = threadName;
	}

	public void start(ServerSocket socket) {
		try  {
			logger.trace(">>> void start(ServerSocket socket)");
			this.socket = socket;
			this.thread = new Thread(this, this.threadName);
			this.thread.start();
		} finally {
			logger.trace("<<< void start(ServerSocket socket)");
		}
		
	}

	public void stop() {
		try {
			logger.trace(">>> void stop()");
			if (this.socket != null) {
				try {
					this.socket.close();
					
				} catch (IOException e) {
					logger.error("Unexpected IOException when calling socket.close()");
					logger.error("Will following exception will give you any hint why this happens?");
					logger.error(Helper.getExceptionAsString(e));	
				}
			}
			
			if (this.thread != null) {
				try {
					this.thread.join();
				} catch (InterruptedException e) {
					logger.error("Unexpected InterruptedException");
					logger.error("Will following exception will give you any hint why this happens?");
					logger.error(Helper.getExceptionAsString(e));	
				}
				this.thread = null;
			} 
		
		} finally {		
			logger.trace("<<< void stop()");
		}
	}

	public synchronized boolean isAlive() {
		logger.trace(">>> boolean isAlive()");
		if (this.thread == null)
			return false;
		boolean isAlive = this.thread.isAlive();
		logger.trace("<<< boolean isAlive()");
		return isAlive;
	}

	/**
	 * That is the function which is running in a separate thread.
	 */
	@Override
	public void run() {
		logger.trace(">>> void run()");
		try {
			for (;;) {
				Socket connectionSocket = this.socket.accept();
				logger.info("TCP-CONN: New connection accepted, Remote = " + socket.getInetAddress().getHostAddress());
				this.onConnectionAccepted(connectionSocket);
			}
		} catch (IOException e) {
			logger.debug(Helper.getExceptionAsString(e));
		}
		logger.trace("<<< void run()");
	}

	public ServerSocket getSocket() {
		return socket;
	}
}
