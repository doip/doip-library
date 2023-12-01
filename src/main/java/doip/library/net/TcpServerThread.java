package doip.library.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;

import doip.library.util.Helper;

/**
 * Implements a TCP Server as a thread which is listening for 
 * incoming connections.
 * 
 * @author Marco Wehnert
 * 
 */
public class TcpServerThread extends TcpServer implements Runnable {

	/** Log4j logger */
	private static Logger logger = LogManager.getLogger(TcpServerThread.class);

	/** Log4j marker for function entry */
	private static Marker enter = MarkerManager.getMarker("ENTER");
	
	/** Log4j marker for function exit */
	private static Marker exit = MarkerManager.getMarker("EXIT'");

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
		Map<String, String> context = getContext();
		if (context != null) {
			for (Map.Entry<String, String> entry : context.entrySet()) {
				ThreadContext.put(entry.getKey(), entry.getValue());
			}
		}		

		logger.trace(enter, ">>> void run()");
		try {
			for (;;) {
				Socket connectionSocket = this.socket.accept();
				logger.info("TCP-CONN: New connection accepted, Remote = " + socket.getInetAddress().getHostAddress());
				this.onConnectionAccepted(connectionSocket);
			}
		} catch (IOException e) {
			logger.debug(Helper.getExceptionAsString(e));
		}
		logger.trace(exit, "<<< void run()");
	}

	public ServerSocket getSocket() {
		return socket;
	}
}
