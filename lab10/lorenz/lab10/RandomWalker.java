package lorenz.lab10;

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
		this(DataSource.getDefaultName());
	}

	public RandomWalker(String name) {
		super(name);

		this.initialState = DataPoint.ZERO;
		this.bounds =
				new DataBox(-1, 1, DataBox.BoundType.REFLECTIVE, -1, 1,
						DataBox.BoundType.REFLECTIVE, -1, 1, DataBox.BoundType.REFLECTIVE);
		this.rng = new Random();
		this.stepSize = DEFAULT_STEP_SIZE;
		this.timeStep = DEFAULT_TIME_STEP;
		this.currentX = initialState.getX();
		this.currentY = initialState.getY();
		this.currentZ = initialState.getZ();
		this.currentT = 0.0;
	}

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
		if (!(stepSize > 0))
			throw new IllegalArgumentException("Bad value stepSize=" + stepSize + " -- must be > 0");
		this.stepSize = stepSize;
	}

	@Override
	public double getTimeStep() {
		return this.timeStep;
	}

	@Override
	public void setTimeStep(double timeStep) {
		if (!(timeStep > 0))
			throw new IllegalArgumentException("Bad value timeStep=" + timeStep + " -- must be > 0");
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

		currentX = bounds.applyXBounds(currentX);
		currentY = bounds.applyXBounds(currentY);
		currentZ = bounds.applyXBounds(currentZ);
	}

	@Override
	protected void doReset() {
		currentX = initialState.getX();
		currentY = initialState.getY();
		currentZ = initialState.getZ();
		currentT = 0.0;
	}
}
