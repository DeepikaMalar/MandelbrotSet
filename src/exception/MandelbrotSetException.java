package exception;

public class MandelbrotSetException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7959862234416616307L;
	String errorMessage = null;
	int errorCode = 0;

	public MandelbrotSetException(String errorMessge) {
		super(errorMessge);
	}
	public MandelbrotSetException() {
		
	}
	public MandelbrotSetException(String errorMessge, int errorCode) {
		super(errorMessge + errorCode);
	}

	
}
