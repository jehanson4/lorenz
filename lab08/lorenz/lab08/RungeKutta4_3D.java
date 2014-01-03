package lorenz.lab08;


import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Fourth-order Runge-Kutta integration, adapted from <i>Numerical Recipes</i>,
 * 2nd Ed., Section 16.1, and customized for systems with three degrees of
 * freedom.
 * 
 * @author jehanson
 * 
 */
public class RungeKutta4_3D extends DataSource {

	private static final String clsName = RungeKutta4_3D.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// =============================================
	// Variables
	// =============================================

	private static final double DEFAULT_TIME_STEP = 0.001;
	
	private final ODESystem_3D odeSystem;
	private DataPoint initialState;
	private double timeStep;
	private double[] currP;
	private double currT;	
	
	// =============================================
	// Creation
	// =============================================

	public RungeKutta4_3D(ODESystem_3D odeSystem) {
		this("", odeSystem, (odeSystem == null) ? DataPoint.ORIGIN
				: odeSystem.getInitialStateHint());
	}

	public RungeKutta4_3D(String name, ODESystem_3D odeSystem) {
		this(name, odeSystem, (odeSystem == null) ? DataPoint.ORIGIN
				: odeSystem.getInitialStateHint());
	}

	public RungeKutta4_3D(String name, ODESystem_3D odeSystem,
			DataPoint initialState) {
		super(name);
		if (odeSystem == null)
			throw new IllegalArgumentException("odeSystem cannot be null");
		if (initialState == null)
			throw new IllegalArgumentException("initialState cannot be null");

		this.odeSystem = odeSystem;
		this.initialState = initialState;
		this.timeStep = DEFAULT_TIME_STEP;
		this.currP = new double[3];
		
		// (OK to call this in ctor b/c it's final)
		this.doReset();
		
	}

	// =============================================
	// Operation
	// =============================================

	
	/**
	 * Returns a defensive copy of this walker's internal data bounds.
	 */
	@Override
	public DataBox getDataBoundsHint() {
		return odeSystem.getDataBoundsHint();
	}

	public DataPoint getInitialState() {
		return this.initialState;
	}

	public void setInitialState(DataPoint initialState) {
		this.initialState = initialState;
	}

	public double getTimeStep() {
		return this.timeStep;
	}

	public void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

	@Override
	public DataPoint getCurrentPoint() {
		return new DataPoint(currP[0], currP[1], currP[2]);
	}

	@Override
	public double getCurrentTime() {
		return currT;
	}

	@Override
	protected void doReset() {
		this.currP[0] = initialState.getX();
		this.currP[1] = initialState.getY();
		this.currP[2] = initialState.getZ();
		this.currT = 0;
	}


	@Override
	protected void doStep() {
		final String mtdName = "doStep";
		if (logger.isLoggable(Level.FINER))
			logger.logp(Level.FINER, clsName, mtdName, "entering");
		
		// temp variables, put into class?
		double deltaT_2 = timeStep * 0.5;
		double deltaT_6 = timeStep/6.0; 
		double[] tmpP = new double[3];		// temp variable: position estimates 
		double[] tmpPDot1 = new double[3];	// temp variable: derivatives at currP
		double[] tmpPDot2 = new double[3];	// temp variable: derivatives estimates
		double[] tmpPDot3 = new double[3];	// temp variable: a derivatives estimate 

		final double nextT_2 = currT+deltaT_2;	// half-way to nextT
		final double nextT = currT+timeStep;
		
		// 1. Take derivatives at currP, use them to calculate 1st estimate of "nextP_2"
		// (i.e., position at nextT_2)
		odeSystem.takeDerivatives(currT,  currP, tmpPDot1);
		for (int i=0; i<3; i++)
			tmpP[i] = currP[i] + deltaT_2 * tmpPDot1[i];

		// 2. take derivatives at our 1st estimate of nextP_2, use them to calculate
		// 2nd estimate of nextP_2
		odeSystem.takeDerivatives(nextT_2, tmpP, tmpPDot2);
		for (int i=0; i<3; i++)
			tmpP[i] = currP[i] + deltaT_2 * tmpPDot2[i];
		
		// 3. take derivatives at our 2nd estimate of nextP_2, use them to calculate
		// 1st estimate of nextP. Also, fiddle with tmpPDot3 in prep for step 4.
		odeSystem.takeDerivatives(nextT_2, tmpP, tmpPDot3);
		for (int i=0; i<3; i++) {
			tmpP[i] = currP[i] + timeStep * tmpPDot3[i];
			tmpPDot3[i] += tmpPDot2[i];
		}
		
		// 4. take derivatives at our 1st estimate of nextP. Combine all estimates
		// with proper weighting to get 2nd and final estimate of nextP.
		odeSystem.takeDerivatives(nextT, tmpP, tmpPDot2);
		for (int i=0; i<3; i++) {
			currP[i] += deltaT_6*(tmpPDot1[i] + tmpPDot2[i] + 2.0*tmpPDot3[i]);
		}
		currT = nextT;
		
		if (logger.isLoggable(Level.FINER))
			logger.logp(Level.FINER, clsName, mtdName,  "Exiting. currP=" + getCurrentPoint());
	}

}
