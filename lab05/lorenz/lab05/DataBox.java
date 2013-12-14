package lorenz.lab05;

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

	public DataBox(DataPoint p0, DataPoint p1) {
		if (p0 == null)
			throw new IllegalArgumentException("p0 cannot be null");
		if (p1 == null)
			throw new IllegalArgumentException("p1 cannot be null");
		
		if (p0.getX() <= p1.getX()) {
			xMin = p0.getX();
			xMax = p1.getX();
		}
		else {
			xMin = p1.getX();
			xMax = p0.getX();
			
		}

		if (p0.getY() <= p1.getY()) {
			yMin = p0.getY();
			yMax = p1.getY();
		}
		else {
			yMin = p1.getY();
			yMax = p0.getY();
			
		}

		if (p0.getZ() <= p1.getZ()) {
			zMin = p0.getZ();
			zMax = p1.getZ();
		}
		else {
			zMin = p1.getZ();
			zMax = p0.getZ();
			
		}

		
	}
	
	public DataBox(DataBox box) {
		if (box == null)
			throw new IllegalArgumentException("box cannot be null");
		xMin = box.getXMin();
		xMax = box.getXMax();
		yMin = box.getYMin();
		yMax = box.getYMax();
		zMin = box.getZMin();
		zMax = box.getZMax();
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

		return "{ min=" + DataPoint.makeTupleString(xMin, yMin, zMin) + ", max="
				+ DataPoint.makeTupleString(xMax, yMax, zMax) + "}";
	}
	
}
