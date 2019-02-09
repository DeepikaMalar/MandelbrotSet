package server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Server {

	public ServerSocket createConnection(int portNumber) {
		ServerSocket serverSock = null;
		try {
			serverSock = new ServerSocket(portNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverSock;
	}

	public void getMessages(ServerSocket serverSocket, BufferedImage[] bufferedImage, String name, double min_c_re,
			double max_c_re, double min_c_im, double max_c_im, int max_n) throws IOException {
		Socket clientSock = serverSocket.accept();
		int count = 0;
		for (BufferedImage image : bufferedImage) {
			count++;
			int max = 256;
			double realNumber = -1;
			double imaginaryNumber = -1.5;
			for (int row = 0; row <= image.getHeight(); row++) {
				for (int col = 0; col <= image.getWidth(); col++) {
					double c_re = realNumber = realNumber + 0.1;
					double c_im = imaginaryNumber = imaginaryNumber - 0.1;
					double x = 0, y = 0;
					int iterations = 0;
					while (x * x + y * y < 4 && iterations < max) {
						double x_new = x * x - y * y + c_re;
						y = 2 * x * y + c_im;
						x = x_new;
						iterations++;
					}
					if (iterations < max) {
						image.setRGB(1, 1, 0xFFFFFF);
					}
				}
			}
			ImageIO.write(image, "png", new File("C:/Users/deepika/Mandelbrot Set-Pgm/" + name + count + ".png"));
		}
		serverSocket.close();
		clientSock.close();
	}
}
