package doip.library.message;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import doip.library.util.Conversion;

public class DoipUdpVehicleAnnouncementMessage extends DoipUdpMessage {
	
	private static Logger logger = LogManager.getLogger(DoipUdpVehicleAnnouncementMessage.class);
	
	private byte[] vin = null;
	private int logicalAddress = 0;
	private byte[] eid = new byte[6];
	private byte[] gid = new byte[6];
	private int furtherActionRequired = 0;
	private int syncStatus = 0;

	public DoipUdpVehicleAnnouncementMessage(byte[] vin, int logicalAddress, byte[] eid, byte[] gid,
			int furtherActionRequired, int syncStatus) {
		this.vin = vin;
		this.logicalAddress = logicalAddress;
		this.eid = eid;
		this.gid = gid;
		this.furtherActionRequired = furtherActionRequired;
		this.syncStatus = syncStatus;
		log(Level.INFO);
	}
	
	public String getMessageName() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_VAM);
	}

	public static String getMessageNameOfClass() {
		return getPayloadTypeAsString(DoipMessage.TYPE_UDP_VAM);
	}

	public void log(Level level) {
		logger.log(level, "----------------------------------------");	
		logger.log(level, "DoIP vehicle announcement message:");
		logger.log(level, "    VIN = " + Conversion.byteArrayToHexString(vin));
		logger.log(level, "    Logical address = " + logicalAddress);
		logger.log(level, "    EID = " + Conversion.byteArrayToHexString(eid));
		logger.log(level, "    GID = " + Conversion.byteArrayToHexString(gid));
		logger.log(level, "    Further action required = " + furtherActionRequired);
		if (syncStatus == -1)  {
			logger.log(level, "    Sync status not available");
		} else {
			logger.log(level, "    Sync status = " + syncStatus);
		}
		logger.log(level, "----------------------------------------");		
	}

	@Override
	public byte[] getMessage() {
		byte[] msg = new byte[41];
		if (syncStatus == -1) msg = new byte[40];
		msg[0] = 0x03;
		msg[1] = (byte) 0xFC;
		msg[2] = 0x00;
		msg[3] = 0x04;
		msg[4] = 0x00;
		msg[5] = 0x00;
		msg[6] = 0x00;
		msg[7] = 33;
		
		System.arraycopy(vin, 0, msg, 8, 17);
		msg[25] = (byte)(this.logicalAddress >> 8);
		msg[26] = (byte)(this.logicalAddress);
		
		System.arraycopy(this.eid, 0, msg, 27, 6);
		System.arraycopy(this.gid, 0, msg, 33, 6);
		msg[39] = (byte) this.furtherActionRequired;
		if (syncStatus != -1) msg[40] = (byte) this.syncStatus;
		return msg;
	}

	public byte[] getVin() {
		return vin;
	}

	public void setVin(byte[] vin) {
		this.vin = vin;
	}

	public int getLogicalAddress() {
		return logicalAddress;
	}

	public void setLogicalAddress(int logicalAddress) {
		this.logicalAddress = logicalAddress;
	}

	public byte[] getEid() {
		return eid;
	}

	public void setEid(byte[] eid) {
		this.eid = eid;
	}

	public byte[] getGid() {
		return gid;
	}

	public void setGid(byte[] gid) {
		this.gid = gid;
	}

	public int getFurtherActionRequired() {
		return furtherActionRequired;
	}

	public void setFurtherActionRequired(int furtherActionRequired) {
		this.furtherActionRequired = furtherActionRequired;
	}

	public int getSyncStatus() {
		return syncStatus;
	}

	public void setSyncStatus(int syncStatus) {
		this.syncStatus = syncStatus;
	}

}
