package doip.library.message;

public class UdsMessage {
	
	public static final int PHYSICAL = 0;
	public static final int FUNCTIONAL = 1;
	
	private int sourceAdrress = 0;
	private int targetAddress = 0;
	private int targetAddressType = PHYSICAL;
	private byte[] message;
	
	@SuppressWarnings("unused")
	private UdsMessage() {}
	
	public UdsMessage(int sourceAddress, int targetAddress, int targetAddressType, byte[] message) {
		this.sourceAdrress = sourceAddress;
		this.targetAddress = targetAddress;
		this.targetAddressType = targetAddressType;
		this.message = message;
	}

	public UdsMessage(int sourceAddress, int targetAddress, byte[] message) {
		this.sourceAdrress = sourceAddress;
		this.targetAddress = targetAddress;
		this.targetAddressType = PHYSICAL;
		this.message = message;
	}

	public int getSourceAdrress() {
		return sourceAdrress;
	}

	public void setSourceAdrress(int sourceAdrress) {
		this.sourceAdrress = sourceAdrress;
	}

	public int getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(int targetAddress) {
		this.targetAddress = targetAddress;
	}

	public int getTargetAddressType() {
		return targetAddressType;
	}

	public void setTargetAddressType(int targetAddressType) {
		this.targetAddressType = targetAddressType;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}
}
