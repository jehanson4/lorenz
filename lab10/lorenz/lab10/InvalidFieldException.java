package lorenz.lab10;

/**
 * 
 * @author jehanson
 */
public class InvalidFieldException extends Exception {

	private static final long serialVersionUID = 7136234348679396045L;

	public InvalidFieldException() {
		super();
	}

	public InvalidFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidFieldException(String message) {
		super(message);
	}

	public InvalidFieldException(Throwable cause) {
		super(cause);
	}

}
