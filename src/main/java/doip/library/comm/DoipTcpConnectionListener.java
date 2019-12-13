package doip.library.comm;

import doip.library.message.DoipTcpAliveCheckRequest;
import doip.library.message.DoipTcpAliveCheckResponse;
import doip.library.message.DoipTcpDiagnosticMessage;
import doip.library.message.DoipTcpDiagnosticMessageNegAck;
import doip.library.message.DoipTcpDiagnosticMessagePosAck;
import doip.library.message.DoipTcpHeaderNegAck;
import doip.library.message.DoipTcpRoutingActivationRequest;
import doip.library.message.DoipTcpRoutingActivationResponse;

public interface DoipTcpConnectionListener {
	
	public void onConnectionClosed(DoipTcpConnection doipTcpConnection);

	public void onDoipTcpDiagnosticMessage(DoipTcpConnection doipTcpConnection, DoipTcpDiagnosticMessage doipMessage);

	public void onDoipTcpDiagnosticMessageNegAck(DoipTcpConnection doipTcpConnection, DoipTcpDiagnosticMessageNegAck doipMessage);
	
	public void onDoipTcpDiagnosticMessagePosAck(DoipTcpConnection doipTcpConnection, DoipTcpDiagnosticMessagePosAck doipMessage);
	
	public void onDoipTcpRoutingActivationRequest(DoipTcpConnection doipTcpConnection, DoipTcpRoutingActivationRequest doipMessage);
	
	public void onDoipTcpRoutingActivationResponse(DoipTcpConnection doipTcpConnection, DoipTcpRoutingActivationResponse doipMessage);
	
	public void onDoipTcpAliveCheckRequest(DoipTcpConnection doipTcpConnection, DoipTcpAliveCheckRequest doipMessage);
	
	public void onDoipTcpAliveCheckResponse(DoipTcpConnection doipTcpConnection, DoipTcpAliveCheckResponse doipMessage);
	
	public void onDoipTcpHeaderNegAck(DoipTcpConnection doipTcpConnection, DoipTcpHeaderNegAck doipMessage);
}
