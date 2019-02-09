package server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;

public interface ConnectionService {
	
	public ServerSocket createConnection(int portNumber);
	public void getMessages(ServerSocket serverSocket,  BufferedImage[] bufferedImage, String name) throws IOException;

}
