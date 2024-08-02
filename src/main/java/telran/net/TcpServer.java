package telran.net;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static telran.net.TcpConfigurationProperties.*;
public class TcpServer implements Runnable{
	Protocol protocol;
	int port;
	boolean running = true;
	private ExecutorService executorService;
	 private static final int MAX_THREADS = 4;
	
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
		this.executorService = Executors.newFixedThreadPool(MAX_THREADS);
	}
	public void shutdown() {
		running = false;
		executorService.shutdown();
		try {
			executorService.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			 executorService.shutdownNow();
		}
	}
	public void run() {
		try(ServerSocket serverSocket = new ServerSocket(port)){
			
			System.out.println("Server is listening on port " + port);
			serverSocket.setSoTimeout(SOCKET_TIMEOUT);
			while(running) {
				try {
					Socket socket = serverSocket.accept();

					TcpClientServerSession session =
							new TcpClientServerSession(socket, protocol, this);
					session.start();
				} catch (SocketTimeoutException e) {
					
				}
				
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}