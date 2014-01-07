package lorenz.lab09;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * x = theta y = phi z = thetaDot
 * 
 * @author jehanson
 */
public class PendulumSystem implements ODESystem_3D {

	private interface InnerODE {
		public void takeDerivatives(double t, double[] p, double[] dpdt);
	}

	/**
	 * For phiDot = 0, we simplify:
	 * 
	 * <pre>
	 * d(thetaDot)/dt = -g/r * sin(theta)
	 * d(theta)/dt    = thetaDot
	 * d(phi)/dt      = 0
	 * </pre>
	 * 
	 * which for <code>p = {theta, phi, thetaDot}</code> gives:
	 * 
	 * <pre>
	 * dpdt[0] = p[2];
	 * dpdt[1] = 0;
	 * dpdt[2] = -g/r * sin(p[0]);
	 * </pre>
	 */
	private class PlanarCase implements InnerODE {

		@Override
		public void takeDerivatives(double t, double[] p, double[] dpdt) {
			dpdt[0] = p[2];
			dpdt[1] = 0;
			dpdt[2] = minusGOverR * Math.sin(p[0]);
		}
	}

	/**
	 * For phiDot != 0, we have:
	 * <pre>
	 * d(thetaDot)/dt = sin(theta) * cos(theta) * (phiDot)^2 - g/r * sin(theta)
	 * d(theta)/dt = thetaDot
	 * d(phi)/dt = phiDot
	 * where:
	 * m * r^2 * (sin(theta))^2 * phiDot = const.
	 * </pre>
	 */
	private class GeneralCase implements InnerODE {

		@Override
		public void takeDerivatives(double t, double[] p, double[] dpdt) {
			
			// p[0] = theta
			// p[1] = phi
			// p[2] = thetaDot
			
			final double sinTheta = Math.sin(p[0]);
			final double rSinTheta = r * sinTheta;
			final double phiDot = lambda/(rSinTheta * rSinTheta);
			dpdt[0] = p[2];
			dpdt[1] = phiDot;
			dpdt[2] = sinTheta * Math.cos(p[0]) * phiDot * phiDot + minusGOverR * sinTheta;
		}

	}

	// =====================================
	// Variables
	// =====================================

	private static final double DEFAULT_R = 1;
	private static final double DEFAULT_G = 9.8;
	private static final double DEFAULT_LAMBDA = 0;

	private static final DataPoint DEFAULT_IC = new DataPoint(7. * Math.PI / 8., 0, 0);

	private double r; // length of pendulum's arm
	private double g; // gravitational acceleration
	private double lambda; // proportional to angular momentum

	private InnerODE innerODE;
	private double minusGOverR;

	// =====================================
	// Creation
	// =====================================

	public PendulumSystem() {
		super();
		setParams(DEFAULT_R, DEFAULT_G, DEFAULT_LAMBDA);
	}

	// =====================================
	// Operation
	// =====================================

	@Override
	public DataBox getDataBoundsHint() {
		// x = theta => [0, PI]
		// y = phi => [0, 2*PI]
		// z = thetaDot => [-10, 10] ? . . . just guessing here . . .
		// plus some margin . . . 
		DataPoint min = new DataPoint(-3.2, -6.4, -10.1);
		DataPoint max = new DataPoint(3.2, 6.4, 10.1);
		return new DataBox(min, max);
	}

	@Override
	public DataPoint getInitialStateHint() {
		return DEFAULT_IC;
	}

	@Override
	public Map<String, Double> getCoefficients() {
		Map<String, Double> coeffs = new HashMap<String, Double>();
		coeffs.put("r", Double.valueOf(r));
		coeffs.put("g", Double.valueOf(g));
		coeffs.put("lambda", Double.valueOf(lambda));
		return coeffs;
	}

	@Override
	public Map<String, Double> getCoefficientsHint() {
		Map<String, Double> coeffs = new HashMap<String, Double>();
		coeffs.put("r", Double.valueOf(DEFAULT_R));
		coeffs.put("g", Double.valueOf(DEFAULT_G));
		coeffs.put("lambda", Double.valueOf(DEFAULT_LAMBDA));
		return coeffs;
	}

	@Override
	public void setCoefficients(Map<String, Double> coefficients) {
		Double vv;

		vv = coefficients.get("r");
		double r2 = (vv == null) ? r : vv.doubleValue();

		vv = coefficients.get("g");
		double g2 = (vv == null) ? g : vv.doubleValue();

		vv = coefficients.get("lambda");
		double lambda2 = (vv == null) ? lambda : vv.doubleValue();

		setParams(r2, g2, lambda2);
	}

	@Override
	public void takeDerivatives(double t, double[] p, double[] dpdt) {
		innerODE.takeDerivatives(t, p, dpdt);
	}

	// ===========================
	// Private
	// ===========================

	private void setParams(double r, double g, double lambda) {
		this.r = r;
		this.g = g;
		this.lambda = lambda;

		this.minusGOverR = -g / r;
		this.innerODE = (lambda == 0) ? new PlanarCase() : new GeneralCase();
	}
}
