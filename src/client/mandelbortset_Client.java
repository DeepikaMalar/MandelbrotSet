package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import exception.MandelbrotSetException;
import server.Server;

public class mandelbortset_Client implements Runnable {
	final String HOSTNAME = "localhost";
	private BufferedImage[] bufferedImage = null;
	private String hostAndPort = null;
	private static double min_c_re = 0;
	private static double max_c_re = 0;
	private static double min_c_im = 0;
	private static double max_c_im = 0;
	private static int max_n = 0;
	private static int width = 0;
	private static int height = 0;
	private static int divisions = 0;

	public mandelbortset_Client(BufferedImage[] bufferedImage, String hostAndPort) {
		this.bufferedImage = bufferedImage;
		this.hostAndPort = hostAndPort;
	}

	public static void main(String args[]) throws IOException, MandelbrotSetException {
		min_c_re = Double.parseDouble(args[0]);
		min_c_im = Double.parseDouble(args[1]);
		max_c_re = Double.parseDouble(args[2]);
		max_c_im = Double.parseDouble(args[3]);
		max_n = Integer.parseInt(args[4]);
		width = Integer.parseInt(args[5]);
		height = Integer.parseInt(args[6]);
		divisions = Integer.parseInt(args[7]);
		// Open file uploader window
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File uploadedFile = jfc.getSelectedFile();
			BufferedImage uploadedImage = ImageIO.read(uploadedFile);
			/*
			 * uploaded image width and height is not 1000 X 1000 then the function will
			 * throw an error message otherwise images is convert as gray scale image and
			 * divided the image as 4 rows and columns and then given to server to do
			 * mandelbrot function.
			 */
			if (uploadedImage.getWidth() != width && uploadedImage.getHeight() != height) {
				throw new MandelbrotSetException("please select image with pixel " + width + "x" + height);
			} else {
				BufferedImage bufferedImage = convertToGrayScaleImage(uploadedImage);
				BufferedImage[] splittedImages = splitGrayscaleImage(bufferedImage, divisions);
				createLoadServer(splittedImages, Arrays.copyOfRange(args, 8, 11));
			}

		}

	}
	/**
	 * method to load server for each port number 
	 * input BufferedImages[]
	 * input host as String []
	 * input ports as String[]
	 */

	private static void createLoadServer(BufferedImage[] splittedImages, String[] hostAndPorts) {
		int startIndex = 0;
		int endIndex = 0;
		int splitLength = Math.round(splittedImages.length / 3);
		ExecutorService executor = Executors.newFixedThreadPool(3);
		for (int i = 0; i < hostAndPorts.length; i++) {
			if (startIndex + splitLength < splittedImages.length) {
				endIndex = startIndex + splitLength + 1;
			} else {
				endIndex = splittedImages.length;
			}
			Runnable server = new mandelbortset_Client(Arrays.copyOfRange(splittedImages, startIndex, endIndex),
					hostAndPorts[i]);
			executor.execute(server);
			startIndex = endIndex;
		}
		executor.shutdown();
		System.out.println("\n process completed");
	}
	/**
	 * method to divided gray scale image as sub images 
	 * input BufferedImage as bufferedImageGray
	 * input division as int
	 * output BufferedImage as splittedImages[].
	 */
	private static BufferedImage[] splitGrayscaleImage(BufferedImage bufferedImageGray, int divisions) {

		int splitImageWidht = bufferedImageGray.getWidth() / divisions; // determines the chunk width and height
		int splitImageHeight = bufferedImageGray.getHeight() / divisions;
		int count = 0;
		BufferedImage splittedImages[] = new BufferedImage[divisions * divisions]; // Image array to hold image chunks
		for (int x = 0; x < divisions; x++) {
			for (int y = 0; y < divisions; y++) {
				splittedImages[count] = new BufferedImage(splitImageWidht, splitImageHeight,
						bufferedImageGray.getType());
				Graphics2D gr = splittedImages[count++].createGraphics();
				gr.drawImage(bufferedImageGray, 0, 0, splitImageWidht, splitImageHeight, splitImageWidht * y,
						splitImageHeight * x, splitImageWidht * y + splitImageWidht,
						splitImageHeight * x + splitImageHeight, null);
				gr.dispose();
			}
		}
		return splittedImages;
	}

	/**
	 * method to convert original image to gray scale image 
	 * input BufferedImage as originalImage
	 * input division as int
	 * output BufferedImage as grayScaleImage.
	 */
	private static BufferedImage convertToGrayScaleImage(BufferedImage originalImage) throws MandelbrotSetException {
		// TODO Auto-generated method stub
		int red = 0;
		int blue = 0;
		int green = 0;
		BufferedImage grayScaleImage = null;
		try {
			grayScaleImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D graphic = grayScaleImage.createGraphics();
			graphic.drawImage(originalImage, 0, 0, Color.WHITE, null);
			for (int i = 0; i < grayScaleImage.getHeight(); i++) {
				for (int j = 0; j < grayScaleImage.getWidth(); j++) {
					Color c = new Color(grayScaleImage.getRGB(j, i));
					red = (int) (c.getRed() * 0.299);
					green = (int) (c.getGreen() * 0.587);
					blue = (int) (c.getBlue() * 0.114);
					Color newColor = new Color(red + green + blue);
					grayScaleImage.setRGB(j, i, newColor.getRGB());
					ImageIO.write(grayScaleImage, "png", new File("C:/Users/deepika/Mandelbrot Set-Pgm/test.png"));

				}
			}
		} catch (Exception e) {
			throw new MandelbrotSetException(e.getMessage());
		}
		return grayScaleImage;
	}

	@Override
	public void run() {
		try {
			Server connectionService = new Server();
			String hostAndPortSplit[] = hostAndPort.split(":");
			ServerSocket serverSocket = connectionService.createConnection(Integer.parseInt(hostAndPortSplit[1]));
			Socket sock = new Socket(hostAndPortSplit[0], Integer.parseInt(hostAndPortSplit[1]));
			connectionService.getMessages(serverSocket, bufferedImage, Thread.currentThread().getName(), min_c_re,
					max_c_re, min_c_im, max_c_im, max_n);
			sock.close();
		} catch (Exception e) {
			try {
				throw new MandelbrotSetException(e.getMessage());
			} catch (MandelbrotSetException e1) {

			}
		}
	}

}
