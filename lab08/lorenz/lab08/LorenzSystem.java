package lorenz.lab08;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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
	private static final double DEFAULT_BETA = 8. / 3.;

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
	public Properties getParameters() {
		final String mtdName = "getParameters";
		if (logger.isLoggable(Level.FINE)) {
			String msg = "entering";
			logger.logp(Level.FINE, clsName, mtdName, msg);
		}
		Properties params = new Properties();
		params.setProperty("sigma", String.valueOf(this.sigma));
		params.setProperty("rho", String.valueOf(this.rho));
		params.setProperty("beta", String.valueOf(this.beta));
		return params;
	}

	@Override
	public Properties getParameterDefaults() {
		final String mtdName = "getParameterDefaults";
		if (logger.isLoggable(Level.FINE)) {
			String msg = "entering";
			logger.logp(Level.FINE, clsName, mtdName, msg);
		}
		Properties params = new Properties();
		params.setProperty("sigma", String.valueOf(DEFAULT_SIGMA));
		params.setProperty("rho", String.valueOf(DEFAULT_RHO));
		params.setProperty("beta", String.valueOf(DEFAULT_BETA));
		return params;
	}

	@Override
	public void setParameters(Properties params) {
		final String mtdName = "setParameters";
		if (logger.isLoggable(Level.FINE)) {
			OutputStream s1 = new ByteArrayOutputStream();
			PrintStream s2 = new PrintStream(s1);
			s2.println("params:");
			try {
				params.store(s2, "");
			}
			catch (IOException e) {
				// Ignore
			}
			s2.close();
			logger.logp(Level.FINE, clsName, mtdName, s1.toString());
		}
		
		try {
			String s = params.getProperty("sigma");
			if (s != null)
				this.sigma = Double.parseDouble(s);
		}
		catch (Exception err) {
			if (logger.isLoggable(Level.FINE)) {
				String msg = "Error reading sigma -- IGNORING -- err=[" + err + "]";
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}
		}

		try {
			String s = params.getProperty("rho");
			if (s != null)
				this.rho = Double.parseDouble(s);
		}
		catch (Exception err) {
			if (logger.isLoggable(Level.FINE)) {
				String msg = "Error reading rho -- IGNORING -- err=" + err;
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}
		}

		try {
			String s = params.getProperty("beta");
			if (s != null)
				this.beta = Double.parseDouble(s);
		}
		catch (Exception err) {
			if (logger.isLoggable(Level.FINE)) {
				String msg = "Error reading beta -- IGNORING -- err=" + err;
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}
		}
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
