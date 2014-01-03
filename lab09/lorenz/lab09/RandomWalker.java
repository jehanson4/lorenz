package lorenz.lab09;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jehanson
 * 
 */
public class RandomWalker extends DataSource {

	private static final String clsName = RandomWalker.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// =============================================
	// Variables
	// =============================================

	private static final double DEFAULT_STEP_SIZE = 0.01;
	private static final double DEFAULT_TIME_STEP = 1E-3;
	
	private DataPoint initialState;
	private final Random rng;
	private DataBox bounds;

	private double stepSize;
	private double timeStep;

	private double currentX;
	private double currentY;
	private double currentZ;
	private double currentT;

	// =============================================
	// Creation
	// =============================================

	public RandomWalker() {
		this(DataSource.getDefaultName(), DataPoint.ORIGIN);
	}

	public RandomWalker(String name) {
		this(name, DataPoint.ORIGIN);
	}
	
	public RandomWalker(DataPoint initialState) {
		this(DataSource.getDefaultName(), initialState);
	}
	
	public RandomWalker(String name, DataPoint initialState) {
		super(name);
		
		if (initialState == null)
			throw new IllegalArgumentException("initialState cannot be null");

		this.initialState = initialState;
		this.bounds = new DataBox(initialState, 2.0);
		this.rng = new Random();
		this.stepSize = DEFAULT_STEP_SIZE;
		this.timeStep = DEFAULT_TIME_STEP;
		this.currentX = initialState.getX();
		this.currentY = initialState.getY();
		this.currentZ = initialState.getZ();
		this.currentT = 0.0;
	}

	// TODO: ctor that takes bounds and max step size as well as initial
	// position

	// =============================================
	// Operation
	// =============================================

	/**
	 * Returns a defensive copy of this walker's internal data bounds.
	 */
	@Override
	public DataBox getDataBoundsHint() {
		return new DataBox(bounds);
	}

	public double getStepSize() {
		return this.stepSize;
	}

	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}

	public double getTimeStep() {
		return this.timeStep;
	}

	public void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

	@Override
	public DataPoint getCurrentPoint() {
		return new DataPoint(currentX, currentY, currentZ);
	}

	@Override
	public double getCurrentTime() {
		return currentT;
	}
	
	@Override
	public void doStep() {
		final String mtdName = "doStep";
		if (logger.isLoggable(Level.FINER))
			logger.logp(Level.FINER, clsName, mtdName, "entering");
		currentX += stepSize * (2 * rng.nextDouble() - 1);
		currentY += stepSize * (2 * rng.nextDouble() - 1);
		currentZ += stepSize * (2 * rng.nextDouble() - 1);
		currentT += timeStep;

		currentX = bounce(currentX, bounds.getXMin(), bounds.getXMax());
		currentY = bounce(currentY, bounds.getYMin(), bounds.getYMax());
		currentZ = bounce(currentZ, bounds.getZMin(), bounds.getZMax());
	}

	@Override
	protected void doReset() {
		currentX = initialState.getX();
		currentY = initialState.getY();
		currentZ = initialState.getZ();
		currentT = 0.0;
	}

	/**
	 * If the point is outside the boundary, shift it back inside.
	 * 
	 * @param v
	 * @param min
	 * @param max
	 * @return
	 */
	private double bounce(double v, double min, double max) {
		if (v < min) {
			return min + (min-v);
		} else if (v > max) {
			return max - (v-max);
		} else
			return v;
	}
}
