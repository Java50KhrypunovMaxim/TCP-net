package telran.net;
import java.io.IOException;
import java.net.*;
public class TcpServer {
    private Protocol protocol;
    private int port;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private final int TIMEOUT = 30000; 

    public TcpServer(Protocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(TIMEOUT); 
            System.out.println("Server is listening on port " + port);
            while (running) {
                try {
                    Socket socket = serverSocket.accept(); 
                    TcpClientServerSession session = new TcpClientServerSession(socket, protocol, this);
                    session.start();
                } catch (SocketTimeoutException e) {
                    
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    } else {
                        System.out.println("Server is shutting down.");
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                throw new RuntimeException(e);
            } else {
                System.out.println("Server is shutting down.");
            }
        }
    }
}