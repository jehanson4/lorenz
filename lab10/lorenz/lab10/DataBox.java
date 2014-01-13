package lorenz.lab10;

/**
 * 3-d rectangular prism
 * 
 * @author jehanson
 * 
 */
public class DataBox {

	// ========================================
	// Inner classes
	// ========================================

	/**
	 * In situations where a DataBox can be thought of as defining boundaries on
	 * a space, the BoundType identifies what happens when a something inside
	 * the space tries to move across a boundary.
	 * 
	 * @author jehanson
	 */
	public enum BoundType {
		PERMEABLE {
			@Override
			public double apply(double v, double vMin, double vMax) {
				return v;
			}
		},

		REFLECTIVE {
			@Override
			public double apply(double v, double vMin, double vMax) {
				if (v < vMin)
					return vMin + (vMin - v);
				else if (v > vMax)
					return vMax - (v - vMax);
				else
					return v;
			}
		},

		PERIODIC {
			@Override
			public double apply(double v, double vMin, double vMax) {
				if (v < vMin)
					return vMax - (vMin - v);
				else if (v > vMax)
					return vMin + (v - vMax);
				else
					return v;
			}
		};

		public abstract double apply(double v, double vMin, double vMax);
	}

	// ========================================
	// Variables
	// ========================================

	private double xMin;
	private double xMax;
	private BoundType xBoundType;

	private double yMin;
	private double yMax;
	private BoundType yBoundType;

	private double zMin;
	private double zMax;
	private BoundType zBoundType;

	// ========================================
	// Creation
	// ========================================

	/**
	 * Creates a DataBox with sides of the given length, centered on the given
	 * point. Bounderies are all BoundType.PERMEABIE
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
		xBoundType = BoundType.PERMEABLE;

		yMin = center.getY() - offset;
		yMax = yMin + size;
		yBoundType = BoundType.PERMEABLE;

		zMin = center.getZ() - offset;
		zMax = zMin + size;
		zBoundType = BoundType.PERMEABLE;
	}

	/**
	 * Creates a DataBox whose bounds are determined by the two points. All
	 * boundaries are BoundType.PERMEABLE
	 * 
	 * @param p0
	 * @param p1
	 */
	public DataBox(DataPoint p0, DataPoint p1) {
		if (p0 == null)
			throw new IllegalArgumentException("p0 cannot be null");
		if (p1 == null)
			throw new IllegalArgumentException("p1 cannot be null");

		setXBounds(p0.getX(),  p1.getX(),  null);
		setYBounds(p0.getY(),  p1.getY(),  null);
		setZBounds(p0.getZ(),  p1.getZ(),  null);
	}

	public DataBox(DataBox box) {
		if (box == null)
			throw new IllegalArgumentException("box cannot be null");
		xMin = box.getXMin();
		xMax = box.getXMax();
		xBoundType = box.getXBoundType();

		yMin = box.getYMin();
		yMax = box.getYMax();
		yBoundType = box.getYBoundType();

		zMin = box.getZMin();
		zMax = box.getZMax();
		zBoundType = box.getZBoundType();
	}

	public DataBox(double x0, double x1, BoundType xtype, double y0, double y1,
			BoundType ytype, double z0, double z1, BoundType ztype) {
		setXBounds(x0, x1, xtype);
		setYBounds(y0, y1, ytype);
		setZBounds(z0, z1, ztype);
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

	public BoundType getXBoundType() {
		return xBoundType;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYMax() {
		return yMax;
	}

	public BoundType getYBoundType() {
		return yBoundType;
	}

	public double getZMin() {
		return zMin;
	}

	public double getZMax() {
		return zMax;
	}

	public BoundType getZBoundType() {
		return zBoundType;
	}

	public DataPoint getMinPoint() {
		return new DataPoint(xMin, yMin, zMin);
	}

	public DataPoint getMaxPoint() {
		return new DataPoint(xMax, yMax, zMax);
	}

	public final void setXBounds(double x0, double x1, BoundType btype) {
		if (!DataPoint.isNumber(x0))
			throw new IllegalArgumentException("x0 must be a finite number");
		if (!DataPoint.isNumber(x1))
			throw new IllegalArgumentException("x1 must be a finite number");
		if (x0 <= x1) {
			xMin = x0;
			xMax = x1;
		}
		else {
			xMin = x1;
			xMax = x0;
		}
		xBoundType = (btype == null) ? BoundType.PERMEABLE : btype;
	}

	public final void setYBounds(double y0, double y1, BoundType btype) {
		if (!DataPoint.isNumber(y0))
			throw new IllegalArgumentException("y0 must be a finite number");
		if (!DataPoint.isNumber(y1))
			throw new IllegalArgumentException("y1 must be a finite number");
		if (y0 <= y1) {
			yMin = y0;
			yMax = y1;
		}
		else {
			yMin = y1;
			yMax = y0;
		}
		yBoundType = (btype == null) ? BoundType.PERMEABLE : btype;
	}

	/**
	 * Sets the Z-component bounds
	 * @param z0 one boundary value. May be either lower or upper bound. Must be a finite number
	 * @param z1 the other boundary value. Must be a finite number.
	 * @param btype the bounary type. May be null.
	 */
	public final void setZBounds(double z0, double z1, BoundType btype) {
		if (!DataPoint.isNumber(z0))
			throw new IllegalArgumentException("z0 must be a finite number");
		if (!DataPoint.isNumber(z1))
			throw new IllegalArgumentException("z1 must be a finite number");
		if (z0 <= z1) {
			zMin = z0;
			zMax = z1;
		}
		else {
			zMin = z1;
			zMax = z0;
		}
		zBoundType = (btype == null) ? BoundType.PERMEABLE : btype;
	}

	public double applyXBounds(double x) {
		return xBoundType.apply(x, xMin, xMax);
	}

	public double applyYBounds(double y) {
		return yBoundType.apply(y, yMin, yMax);
	}

	public double applyZBounds(double z) {
		return zBoundType.apply(z, zMin, zMax);
	}

	public DataPoint applyBounds(DataPoint p) {
		return new DataPoint(applyXBounds(p.getX()), applyYBounds(p.getY()),
				applyZBounds(p.getZ()));
	}

	/**
	 * Enlarges this databox to the minimum extent necessary so that the given
	 * point is inside it or on its boundary.
	 * 
	 * @param pt
	 *            The point in question
	 */
	public void cover(DataPoint pt) {
		final double px = pt.getX();
		if (px < xMin)
			xMin = px;
		if (px > xMax)
			xMax = px;

		final double py = pt.getY();
		if (py < yMin)
			yMin = py;
		if (py > yMax)
			yMax = py;

		final double pz = pt.getZ();
		if (pz < zMin)
			zMin = pz;
		if (pz > zMax)
			zMax = pz;

	}

	/**
	 * Enlarges this databox to the minimum extent necessary so that every point
	 * in the given box is inside it or on its boundary.
	 * 
	 * @param box
	 *            The box in question
	 */
	public void cover(DataBox box) {
		cover(box.getMinPoint());
		cover(box.getMaxPoint());
	}

	@Override
	public String toString() {
		return "{ min=" + DataPoint.makeTupleString(xMin, yMin, zMin) + ", max="
				+ DataPoint.makeTupleString(xMax, yMax, zMax) + ", bounds=[" + xBoundType
				+ ", " + yBoundType + ", " + zBoundType + "]}";
	}
}
