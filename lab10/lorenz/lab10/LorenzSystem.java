package lorenz.lab10;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Lorenz equations
 * 
 * <pre>
 * dx/dt = sigma*(y-x)
 * dy/dt = x*(rho-z) - y
 * dz/dt = x*y - beta*z;
 * </pre>
 * 
 * @author jehanson
 */
public class LorenzSystem extends AbstractODESystem_3D {

	private static final String clsName = LorenzSystem.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ==================================
	// Variables
	// ==================================

	private static final double SIGMA_DEFAULT = 10;
	private static final double RHO_DEFAULT = 28;
	private static final double BETA_DEFAULT = 2.667;

	public static final String SIGMA_LABEL = "\u03C3";
	public static final String RHO_LABEL = "\u03C1";
	public static final String BETA_LABEL = "\u03B2";
	
	private double sigma;
	private double rho;
	private double beta;

	// ==================================
	// Creation
	// ==================================

	public LorenzSystem() {
		super();
		sigma = SIGMA_DEFAULT;
		rho = RHO_DEFAULT;
		beta = BETA_DEFAULT;
	}

	// ==================================
	// Operation
	// ==================================

	@Override
	public Map<String, Double> getCoefficients() {
		final String mtdName = "getCoefficients";
		if (logger.isLoggable(Level.FINE)) {
			String msg = "entering";
			logger.logp(Level.FINE, clsName, mtdName, msg);
		}
		Map<String, Double> coeffs = new LinkedHashMap<String, Double>();
		coeffs.put(SIGMA_LABEL, Double.valueOf(this.sigma));
		coeffs.put(RHO_LABEL, Double.valueOf(this.rho));
		coeffs.put(BETA_LABEL, Double.valueOf(this.beta));
		return coeffs;
	}

	@Override
	public Map<String, Double> getCoefficientsHint() {
		final String mtdName = "getCoefficientsHint";
		if (logger.isLoggable(Level.FINE)) {
			String msg = "entering";
			logger.logp(Level.FINE, clsName, mtdName, msg);
		}
		Map<String, Double> coeffs = new LinkedHashMap<String, Double>();
		coeffs.put(SIGMA_LABEL, Double.valueOf(SIGMA_DEFAULT));
		coeffs.put(RHO_LABEL, Double.valueOf(RHO_DEFAULT));
		coeffs.put(BETA_LABEL, Double.valueOf(BETA_DEFAULT));
		return coeffs;
	}

	@Override
	public void setCoefficients(Map<String, Double> coeffs) {
		final String mtdName = "setCoefficients";
		if (logger.isLoggable(Level.FINE)) {
			String msg = "entering";
			logger.logp(Level.FINE, clsName, mtdName, msg);
		}
		
		Double v;
		
		v = coeffs.get(SIGMA_LABEL);
		if (v != null)
			this.sigma = v.doubleValue();
		v = coeffs.get(RHO_LABEL);
		if (v != null)
			this.rho = v.doubleValue();
		v = coeffs.get(BETA_LABEL);
		if (v != null)
			this.beta = v.doubleValue();
	}

	@Override
	public DataBox getDataBounds() {
		DataPoint p0 = new DataPoint(-18, -18, 0);
		DataPoint p1 = new DataPoint(18, 18, 50);
		return new DataBox(p0, p1);
	}

	@Override
	public DataPoint getInitialStateHint() {
		return new DataPoint(1, 1, 20);
	}

	@Override
	public void takeDerivatives(double t, double[] p, double[] dpdt) {
		dpdt[0] = sigma * (p[1] - p[0]);
		dpdt[1] = p[0] * (rho - p[2]) - p[1];
		dpdt[2] = p[0] * p[1] - beta * p[2];
	}

}
