package doip.library.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class Helper {

	/**
	 * Returns the path of am file. The returned string has always a '/' at the end.
	 * 
	 * @param file The file from which the path shall be determined
	 * @return The path of the file
	 */
	public static String getPathOfFile(String file) {
		if (file == null)
			return "./";
		if (file.length() == 0)
			return "./";
		if (file.contains("/") == false) {
			return "./";
		}
		return file.substring(0, file.lastIndexOf("/") + 1);
	}

	/**
	 * Concatenates two byte arrays
	 * 
	 * @param a First byte array
	 * @param b Second byte array
	 * @return Concatenated byte array
	 */
	/*
	static public byte[] concat(byte[] a, byte[] b) {
		int lenA = a.length;
		int lenB = b.length;
		byte[] both = new byte[lenA + lenB];
		System.arraycopy(a, 0, both, 0, lenA);
		System.arraycopy(b, 0, both, lenA, lenB);
		return both;
	}*/
	
	static public byte[] concat(byte[]... args) {
		int totalLength = 0;
		for (int i = 0; i < args.length; i++)  {
			totalLength += args[i].length;
		}
		byte[] result = new byte[totalLength];
		
		int destPosOfNextArray = 0;
		
		for (int i = 0; i< args.length; i++) {
			System.arraycopy(args[i], 0, result, destPosOfNextArray, args[i].length);
			destPosOfNextArray += args[i].length;
		}
				
		return result;
	}

	/**
	 * Returns an exception including its stack trace as string.
	 * @param e The exception
	 * @return The exception as string
	 */
	public static String getExceptionAsString(Throwable e) {
		StringBuilder s = new StringBuilder(4096);
		String message = e.getMessage();
		if (message != null) {
			s.append(message);
		}
		s.append("\n");
		s.append(e.getClass().getName());
		s.append("\n");
		StackTraceElement[] elements = e.getStackTrace();
		for (StackTraceElement element : elements) {
			s.append("    ");
			s.append(element);
			s.append("\n");
		}
		return s.toString();
	}

	public static MulticastSocket createUdpSocket(InetAddress localAddress, int localPort, InetAddress multicast)
			throws IOException {
		MulticastSocket socket = null;
		if (localAddress == null) {
			if (localPort == 0) {
				socket = new MulticastSocket();
			} else {
				socket = new MulticastSocket(localPort);
			}
		} else {
			InetSocketAddress socketAddress = new InetSocketAddress(localAddress, localPort);
			socket = new MulticastSocket(socketAddress);
		}

		if (multicast != null) {
			socket.joinGroup(multicast);
		}

		return socket;

	}

	public static ServerSocket createTcpServerSocket(InetAddress localAddress, int localPort) throws IOException {
		ServerSocket socket = null;
		if (localAddress == null) {
			socket = new ServerSocket(localPort);
		} else {
			socket = new ServerSocket(localPort, 256, localAddress);
		}
		return socket;
	}

	public static Socket createTcpClientSocket(InetAddress remoteAddress, int remotePort) throws IOException {
		Socket socket = null;
		socket = new Socket(remoteAddress, remotePort);
		return socket;
	}
}
