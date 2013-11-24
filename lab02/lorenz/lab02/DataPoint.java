package lorenz.lab02;

/**
 * A point in a 3-dimensional Euclidean space. Appropriate for 
 * describing the state of the Lorenz system at a particular time.
 * <p>
 * DataPoint also hase an optional field that can hold a timestamp 
 * value, such as the time at which the system was in state (x,y,z).
 * If the timestamp is not specified, it defaults to Double.NaN.
 * <p>
 * DataPoint instances are immutable.
 * 
 * @author jehanson4
 */
public class DataPoint {
	private final double x;
	private final double y;
	private final double z;
	private final double t;
	
	public DataPoint() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.t = Double.NaN;
	}
	
	public DataPoint(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = Double.NaN;
	}

	public DataPoint(double x, double y, double z, double t) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = t;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public boolean hasTimestamp() {
		return isNumber(t);
	}
	
	public double getT() {
		return t;
	}
	
	// =============================
	// Private
	// =============================

	private boolean isNumber(double x) {
		return !(Double.isInfinite(t) || Double.isNaN(t));
	}
}
