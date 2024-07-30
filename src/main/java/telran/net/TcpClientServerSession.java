package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread{
	Socket socket;
	Protocol protocol;
	TcpServer tcpServer;

	public TcpClientServerSession(Socket socket, Protocol protocol, TcpServer tcpServer) {
		this.socket = socket;
		this.protocol = protocol;
		this.tcpServer = tcpServer;
		try {
            socket.setSoTimeout(60000); 
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
	}
	public void run() {
		try 	(Socket s = socket; // само закроет сокет!
				BufferedReader receiver =
				new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())){
			String line = null;
			while((line = receiver.readLine()) != null) {
				String responseStr = protocol.getResponseWithJSON(line);
				sender.println(responseStr);
			}
		 } catch (SocketTimeoutException e) {
	            if (!tcpServer.running) {
	                System.out.println("Session closed via to server shutdown.");
	            } else {
	                System.out.println("Session closed via to idle timeout.");
	            }
	        } catch (IOException e) {
	            System.out.println(e);
	        } 
	    }
	}