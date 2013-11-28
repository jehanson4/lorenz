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

	public DataBox(DataPoint p0, DataPoint p1) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	/**
	 * Creates a DataBox with sides of the given length, centered on the given point.
	 * @param center
	 * @param size
	 * @return
	 */
	public static DataBox cubeAt(DataPoint center, double size) {
		throw new UnsupportedOperationException("not implemented");
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
	
}
