package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread {
    private Socket socket;
    private Protocol protocol;
    private TcpServer tcpServer;
    private int time = 30000;

    public TcpClientServerSession(Socket socket, Protocol protocol, TcpServer tcpServer) {
        this.socket = socket;
        this.protocol = protocol;
        this.tcpServer = tcpServer;
        try {
            socket.setSoTimeout(time); 
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try (Socket soc = socket; 
             BufferedReader receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintStream sender = new PrintStream(socket.getOutputStream())) {

            String line;
            while ((line = receiver.readLine()) != null) {
                String responseStr = protocol.getResponseWithJSON(line);
                sender.println(responseStr);
            }
        } catch (SocketTimeoutException e) {
            if (!tcpServer.isRunning()) {
                System.out.println("Session closed due to server shutdown.");
            } else {
                System.out.println("Session closed due to idle timeout.");
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}