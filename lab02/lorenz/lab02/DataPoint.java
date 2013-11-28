package lorenz.lab02;

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
		assert (isNumber(x) && isNumber(y) && isNumber(z));
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

	// =============================
	// Private
	// =============================

	private boolean isNumber(double x) {
		return !(Double.isInfinite(x) || Double.isNaN(x));
	}
}
