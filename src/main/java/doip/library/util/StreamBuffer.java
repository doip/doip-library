package doip.library.util;

import java.util.Arrays;

import doip.logging.LogManager;
import doip.logging.Logger;

public class StreamBuffer {
	
	private Logger logger = LogManager.getLogger(StreamBuffer.class);
	
	private volatile byte[] data = new byte[0];
	
	public synchronized byte[] getData() {
		return data;
	}
	
	public synchronized int getLength() {
		return this.data.length;
	}
	
	public synchronized void append(byte[] newData) {
		this.logger.debug("Append: " + newData.length + " bytes: "+ Conversion.byteArrayToHexString(newData));
		int newLength = this.data.length + newData.length;
		byte[] newBuffer = new byte[newLength];
		System.arraycopy(data, 0, newBuffer, 0, this.data.length);
		System.arraycopy(newData, 0, newBuffer, this.data.length, newData.length);
		this.data = newBuffer;
	}
	
	public synchronized byte[] remove(int length) {
		this.logger.debug("Remove " + length + " bytes");
		if (length >= this.data.length ) {
			byte[] ret = this.data;
			this.data = new byte[0];
			return ret;
		}
		
		byte[] ret = Arrays.copyOf(this.data, length);
		int remainingLength = this.data.length - length;
		byte[] remaining = new byte[remainingLength];
		System.arraycopy(this.data, length, remaining, 0, remainingLength);
		this.data = remaining;
		return ret;
	}
	
	public synchronized void clear() {
		this.logger.debug("Remove all bytes");
		this.data = new byte[0];
	}
}
