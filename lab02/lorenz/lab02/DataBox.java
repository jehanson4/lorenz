package lorenz.lab02;

/**
 * 3-d rectangular prism
 * 
 * @author jehanson
 * 
 */
public class DataBox {

	// ========================================
	// Variables
	// ========================================

	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	private double zMin;
	private double zMax;

	// ========================================
	// Creation
	// ========================================

	/**
	 * Creates a DataBox with sides of the given length, centered on the given
	 * point.
	 * 
	 * @param center
	 * @param size
	 */
	public DataBox(DataPoint center, double size) {
		if (center == null)
			throw new IllegalArgumentException("center cannot be null");
		if (!(size > 0))
			throw new IllegalArgumentException("size must be > 0");

		final double offset = 0.5 * size;
		xMin = center.getX() - offset;
		xMax = xMin + size;
		yMin = center.getY() - offset;
		yMax = yMin + size;
		zMin = center.getZ() - offset;
		zMax = zMin + size;
	}

	// ========================================
	// Operation
	// ========================================

	public double getXMin() {
		return xMin;
	}

	public double getXMax() {
		return xMax;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYMax() {
		return yMax;
	}

	public double getZMin() {
		return zMin;
	}

	public double getZMax() {
		return zMax;
	}

	public String toString() {

		return "[" + DataPoint.makeTupleString(xMin, yMin, zMin) + ", "
				+ DataPoint.makeTupleString(xMax, yMax, zMax) + "]";
	}
}
