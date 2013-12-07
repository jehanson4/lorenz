package lorenz.lab03;

/**
 * A point in a 3-dimensional Euclidean space, appropriate for describing the
 * state of the Lorenz system at some instant of time.
 * <p>
 * DataPoint instances are immutable.
 * 
 * @author jehanson4
 */
public class DataPoint {

	// ============================
	// Variables
	// ============================

	public static final DataPoint ORIGIN = new DataPoint(0, 0, 0);

	private final double x;
	private final double y;
	private final double z;

	// ============================
	// Creation
	// ============================

	public DataPoint(double x, double y, double z) {
		if (!isNumber(x))
			throw new IllegalArgumentException("x must be a finite number");
		if (!isNumber(y))
			throw new IllegalArgumentException("y must be a finite number");
		if (!isNumber(z))
			throw new IllegalArgumentException("z must be a finite number");
		
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// ============================
	// Operation
	// ============================

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public String toString() {
		return makeTupleString(x,y,z);
	}
	
	public static String makeTupleString(double x, double y, double z) {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	// =============================
	// Private
	// =============================

	private boolean isNumber(double x) {
		return !(Double.isInfinite(x) || Double.isNaN(x));
	}
}
