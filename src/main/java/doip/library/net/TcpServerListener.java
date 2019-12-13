package doip.library.net;

import java.net.Socket;

public interface TcpServerListener {
	public void onConnectionAccepted(TcpServer tcpServer, Socket socket);
}
