package telran.net;
import java.net.*;
public class TcpServer {
	Protocol protocol;
	int port;
	int time = 30000;
	boolean running = true;
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
	}
	public void shutdown() {
		running = false;
	}
	public void run() {
		try(ServerSocket serverSocket = new ServerSocket(port)){
			serverSocket.setSoTimeout(time);
			System.out.println("Server is listening on port " + port);
			while(running) {
				Socket socket;
				try {
					socket = serverSocket.accept();
					TcpClientServerSession session =
					new TcpClientServerSession(socket, protocol, this);
				session.start();
				} catch(SocketTimeoutException e) {
					System.out.println("Server socket timed out.");
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}