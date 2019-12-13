package doip.library.message;

public abstract class DoipTcpMessage extends DoipMessage {

	public abstract void parsePayload(byte[] payload);
}
