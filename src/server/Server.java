package server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

import exception.MandelbrotSetException;

public class Server {
	/**
	 * method to create server connection for each port number 
	 * input portNumber as int
	 * output ServerSocket as serverSocket.*/
	public ServerSocket createConnection(int portNumber) throws MandelbrotSetException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (Exception e) {
			throw new MandelbrotSetException(e.getMessage());
		}
		return serverSocket;
	}
	/**
	 * method to generate mandelbrot for each images over iteration 
	 * input ServerSocket as serverSocket
	 * input BufferedImage[] as bufferedImage
	 * input Thread name as String, min_c_re, min_c_im, max_c_im, max_c_re, max_n ad double
	 **/
	public void getMessages(ServerSocket serverSocket, BufferedImage[] bufferedImage, String name, double min_c_re,
			double max_c_re, double min_c_im, double max_c_im, int max_n) throws IOException, MandelbrotSetException {
		Socket clientSock = null;
		try {
			clientSock = serverSocket.accept();
			int count = 0;
			for (BufferedImage image : bufferedImage) {
				count++;
				int max = 256;
				for (int row = 0; row <= image.getHeight(); row++) {
					for (int col = 0; col <= image.getWidth(); col++) {
						double c_re = min_c_re = min_c_re + 0.1;
						double c_im = min_c_im = min_c_im - 0.1;
						double x = 0, y = 0;
						int iterations = 0;
						while (x * x + y * y < 4 && iterations < max && c_re != max_c_re && c_im != max_c_im) {
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
		} catch (Exception e) {
			throw new MandelbrotSetException(e.getMessage());
		} finally {
			serverSocket.close();
			clientSock.close();
		}

	}
}
