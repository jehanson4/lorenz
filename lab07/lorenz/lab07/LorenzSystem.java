package lorenz.lab07;

import java.util.Properties;

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

	// ==================================
	// Variables
	// ==================================

	private static final double DEFAULT_SIGMA = 10;
	private static final double DEFAULT_RHO   = 28;
	private static final double DEFAULT_BETA  = 8. / 3.;

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
		Properties params = new Properties();
		params.setProperty("sigma", String.valueOf(this.sigma));
		params.setProperty("rho", String.valueOf(this.rho));
		params.setProperty("beta", String.valueOf(this.beta));
		return params;
	}

	@Override
	public Properties getParameterDefaults() {
		Properties params = new Properties();
		params.setProperty("sigma", String.valueOf(DEFAULT_SIGMA));
		params.setProperty("rho", String.valueOf(DEFAULT_RHO));
		params.setProperty("beta", String.valueOf(DEFAULT_BETA));
		return params;
	}

	@Override
	public void setParameters(Properties params) {
		try {
			this.sigma = Integer.parseInt(params.getProperty("sigma"));
		}
		catch (Exception err) {
			// Ignore
		}
		
		try {
			this.rho = Integer.parseInt(params.getProperty("rho"));
		}
		catch (Exception err) {
			// Ignore
		}

		try {
			this.beta = Integer.parseInt(params.getProperty("beta"));
		}
		catch (Exception err) {
			// Ignore
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
