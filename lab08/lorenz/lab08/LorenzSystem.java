package lorenz.lab08;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
public class LorenzSystem implements ODESystem_3D {

	private static final String clsName = LorenzSystem.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ==================================
	// Variables
	// ==================================

	private static final double DEFAULT_SIGMA = 10;
	private static final double DEFAULT_RHO = 28;
	private static final double DEFAULT_BETA = 2.667;

	private static final String SIGMA_KEY = "sigma";
	private static final String RHO_KEY = "rho";
	private static final String BETA_KEY = "beta";
	
	private double sigma;
	private double rho;
	private double beta;

	// ==================================
	// Creation
	// ==================================

	public LorenzSystem() {
		super();
		sigma = DEFAULT_SIGMA;
		rho = DEFAULT_RHO;
		beta = DEFAULT_BETA;
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
		Map<String, Double> coeffs = new HashMap<String, Double>();
		coeffs.put(SIGMA_KEY, Double.valueOf(this.sigma));
		coeffs.put(RHO_KEY, Double.valueOf(this.rho));
		coeffs.put(BETA_KEY, Double.valueOf(this.beta));
		return coeffs;
	}

	@Override
	public Map<String, Double> getCoefficientsHint() {
		final String mtdName = "getCoefficientsHint";
		if (logger.isLoggable(Level.FINE)) {
			String msg = "entering";
			logger.logp(Level.FINE, clsName, mtdName, msg);
		}
		Map<String, Double> coeffs = new HashMap<String, Double>();
		coeffs.put(SIGMA_KEY, Double.valueOf(DEFAULT_SIGMA));
		coeffs.put(RHO_KEY, Double.valueOf(DEFAULT_RHO));
		coeffs.put(BETA_KEY, Double.valueOf(DEFAULT_BETA));
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
		
		v = coeffs.get(SIGMA_KEY);
		if (v != null)
			this.sigma = v.doubleValue();
		v = coeffs.get(RHO_KEY);
		if (v != null)
			this.rho = v.doubleValue();
		v = coeffs.get(BETA_KEY);
		if (v != null)
			this.beta = v.doubleValue();
	}

	@Override
	public DataBox getDataBoundsHint() {
		DataPoint p0 = new DataPoint(-18, -18, 0);
		DataPoint p1 = new DataPoint(18, 18, 50);
		return new DataBox(p0, p1);
	}

	@Override
	public DataPoint getInitialStateHint() {
		// TODO Auto-generated method stub
		return new DataPoint(1, 1, 20);
	}

	@Override
	public void takeDerivatives(double t, double[] p, double[] dpdt) {
		dpdt[0] = sigma * (p[1] - p[0]);
		dpdt[1] = p[0] * (rho - p[2]) - p[1];
		dpdt[2] = p[0] * p[1] - beta * p[2];
	}

}
