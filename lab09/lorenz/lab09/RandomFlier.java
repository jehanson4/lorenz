package lorenz.lab09;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jehanson
 * 
 */
public class RandomFlier extends DataSource {

	private static final String clsName = RandomFlier.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// =============================================
	// Variables
	// =============================================

	private static final DataPoint DEFAULT_INITIAL_VELOCITY = new DataPoint(0., 0., 0.);
	private static final double DEFAULT_VMAX = 1.0;
	private static final double DEFAULT_PULSE_SIZE = 0.05;
	private static final double DEFAULT_TIME_STEP = 1E-3;
	
	private DataPoint initialState;
	private DataPoint initialVelocity;
	private final Random rng;
	private DataBox bounds;

	private double pulseSize;
	private double vMax;
	private double timeStep;

	private double currentVX;
	private double currentVY;
	private double currentVZ;
	private double currentX;
	private double currentY;
	private double currentZ;
	private double currentT;

	// =============================================
	// Creation
	// =============================================

	public RandomFlier() {
		this(DataSource.getDefaultName(), DataPoint.ORIGIN);
	}

	public RandomFlier(String name) {
		this(name, DataPoint.ORIGIN);
	}

	public RandomFlier(DataPoint initialState) {
		this(DataSource.getDefaultName(), initialState);
	}

	public RandomFlier(String name, DataPoint initialState) {
		super(name);

		if (initialState == null)
			throw new IllegalArgumentException("initialState cannot be null");

		this.initialState = initialState;
		this.initialVelocity = DEFAULT_INITIAL_VELOCITY;
		this.bounds = new DataBox(initialState, 2.0);
		this.rng = new Random();
		this.pulseSize = DEFAULT_PULSE_SIZE;
		this.vMax = DEFAULT_VMAX;
		this.timeStep = DEFAULT_TIME_STEP;
		
		// OK to call mtd from ctor iff it's final.
		this.doReset();
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

	public double getPulseSize() {
		return this.pulseSize;
	}

	public void setPulseSize(double pulseSize) {
		this.pulseSize = pulseSize;
	}

	public double getTimeStep() {
		return this.timeStep;
	}

	public void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

	public double getVMax() {
		return this.vMax;
	}

	public void setVMax(double vMax) {
		this.vMax = vMax;
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

		currentVX += pulseSize * (2 * rng.nextDouble() - 1);
		if (Math.abs(currentVX) > vMax)
			currentVX = Math.signum(currentVX) * vMax;	
		
		currentVY += pulseSize * (2 * rng.nextDouble() - 1);
		if (Math.abs(currentVY) > vMax)
			currentVY = Math.signum(currentVY) * vMax;	
		
		currentVZ += pulseSize * (2 * rng.nextDouble() - 1);
		if (Math.abs(currentVZ) > vMax)
			currentVZ = Math.signum(currentVZ) * vMax;	
		
		currentX += currentVX * timeStep;
		currentY += currentVY * timeStep;
		currentZ += currentVZ * timeStep;
		currentT += timeStep;

		// Rough approximation of bouncing, good only if step size is << data
		// bounds, but maybe it'll be good enough.
		if (currentX < bounds.getXMin() || currentX > bounds.getXMax()) {
			currentVX = -currentVX;
			currentX += 2 * (currentVX * timeStep);
		}
		if (currentY < bounds.getYMin() || currentY > bounds.getYMax()) {
			currentVY = -currentVY;
			currentY += 2 * (currentVY * timeStep);
		}
		if (currentZ < bounds.getZMin() || currentZ > bounds.getZMax()) {
			currentVZ = -currentVZ;
			currentZ += 2 * (currentVZ * timeStep);
		}
	}

	@Override
	protected final void doReset() {
		currentVX = initialVelocity.getX();
		currentVY = initialVelocity.getY();
		currentVZ = initialVelocity.getZ();
		currentX = initialState.getX();
		currentY = initialState.getY();
		currentZ = initialState.getZ();
		currentT = 0.0;
	}

}
