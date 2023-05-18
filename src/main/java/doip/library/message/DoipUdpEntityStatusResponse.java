package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DoipUdpEntityStatusResponse extends DoipUdpMessage {
	
	private static Logger logger = LogManager.getLogger(DoipUdpEntityStatusResponse.class);
	
	private int nodeType = 0;
	
	private int maxNumberOfSockets = 0;
	
	private int currentNumberOfSockets = 0;
	
	private long maxDataSize = 0;

	public DoipUdpEntityStatusResponse(int nodeType, int maxNumberOfSockets, int currentNumberOfSockets,
			long maxDataSize) {
		this.nodeType = nodeType;
		this.maxNumberOfSockets = maxNumberOfSockets;
		this.currentNumberOfSockets = currentNumberOfSockets;
		this.maxDataSize = maxDataSize;
		this.log(Level.INFO);
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_ENTITY_STATUS_RES);
	}
	
	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_ENTITY_STATUS_RES);
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");
		logger.log(level, "DoIP entity status response:");
		logger.log(level, "    Node type                 = " + this.nodeType);
		logger.log(level, "    Maximum number of sockets = " + this.maxNumberOfSockets);
		logger.log(level, "    Current number of sockets = " + this.currentNumberOfSockets);
		logger.log(level, "    Maximum data size         = " + this.maxDataSize);
		logger.log(level, "----------------------------------------");
	}

	@Override
	public byte[] getMessage() {
		byte[] msg = new byte[15];
		msg[0] = 0x03;
		msg[1] = (byte) 0xFC;
		msg[2] = 0x40;
		msg[3] = 0x02;
		msg[4] = 0x00;
		msg[5] = 0x00;
		msg[6] = 0x00;
		msg[7] = 0x07;
		
		msg[8] = (byte)this.nodeType;
		msg[9] = (byte)this.maxNumberOfSockets;
		msg[10] = (byte)this.currentNumberOfSockets;
		
		msg[11] = (byte)(this.maxDataSize >> 24);
		msg[12] = (byte)(this.maxDataSize >> 16);
		msg[13] = (byte)(this.maxDataSize >> 8);
		msg[14] = (byte)(this.maxDataSize);
		return msg;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public int getMaxNumberOfSockets() {
		return maxNumberOfSockets;
	}

	public void setMaxNumberOfSockets(int maxNumberOfSockets) {
		this.maxNumberOfSockets = maxNumberOfSockets;
	}

	public int getCurrentNumberOfSockets() {
		return currentNumberOfSockets;
	}

	public void setCurrentNumberOfSockets(int currentNumberOfSockets) {
		this.currentNumberOfSockets = currentNumberOfSockets;
	}

	public long getMaxDataSize() {
		return maxDataSize;
	}

	public void setMaxDataSize(long maxDataSize) {
		this.maxDataSize = maxDataSize;
	}
}
