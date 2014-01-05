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

	private class PlanarCase implements InnerODE {

		@Override
		public void takeDerivatives(double t, double[] p, double[] dpdt) {
			// For phiDot = 0, we simplify:
			// d(theta)/dt = thetaDot
			// d(thetaDot)/dt = -g/L sin(theta)
			// --------------------------------
			// p[0] = theta => dx/dt = z;
			// p[1] = phi => dydt = 0;
			// p[2] = thetaDot => dzdt = -g/L sin(x)

			dpdt[0] = p[2];
			dpdt[1] = 0;
			dpdt[2] = minusGOverL * Math.sin(p[0]);
		}
	}

	private class GeneralCase implements InnerODE {

		@Override
		public void takeDerivatives(double t, double[] p, double[] dpdt) {
			// d^2(theta)/dt^2 = sin(theta) * cos(theta) * (d(phi)/dt)^2 - g/L *
			// sin(theta)
			//
			// * m*L^2*sin(theta^2) * d(phi)/dt = const
			// *
			// * theta
			// * thetaDot
			// * phi
			// * phiDot
			// *
			// * d(theta)/dt = thetaDot
			// * d(thetaDot)/dt = sin(theta)*cos(theta)*phiDot^2 - g/L*sin(theta)
			// from
			// m * L^2 * (sin(theta))^2 * phiDot = const
			// we get:
			// phiDot = gamma / (sin(theta))^2
			// so therefore
			// d(thetaDot)/dt = sin(theta)*cos(theta)*(gamma/(sin(theta))^2)^2
			
			// * d(phiDot)/dt = 0
			// *
			// * FOr phiDot = 0, we simplify:
			// *
			// * d(theta)/dt = thetaDot
			// * d(thetaDot)/dt = -g/L sin(theta)
			// *
			// *
			// * 1. \ddot\theta =
			// \sin\theta\cos\theta\dot{\phi}^2-\frac{g}{L}\sin\theta
			// *
			// * 2. m L^2 \sin\theta^2 \dot{\phi} = const
		}

	}

	// =====================================
	// Variables
	// =====================================

	private static final double DEFAULT_L = 1;
	private static final double DEFAULT_G = 9.8;
	private static final double DEFAULT_GAMMA = 0;

	// x = theta => PI/2
	// y = phi => 0
	// z = thetaDot => -0.5
	private static final DataPoint DEFAULT_IC = new DataPoint(Math.PI / 2, 0, -0.5);

	private double L; // length of pendumum's arm
	private double g; // gravity
	private double gamma; // rotational coefficient: if 0, motion is planer

	private InnerODE innerODE;
	private double minusGOverL;

	// =====================================
	// Creation
	// =====================================

	public PendulumSystem() {
		super();
		setParams(DEFAULT_L, DEFAULT_G, DEFAULT_GAMMA);
	}

	// =====================================
	// Operation
	// =====================================

	@Override
	public DataBox getDataBoundsHint() {
		// x = theta => [0, PI]
		// y = phi => [0, 2*PI]
		// z = thetaDot => [-10, 10] ?
		// plus some margin . . . just guessing here . . .
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
		coeffs.put("L", Double.valueOf(L));
		coeffs.put("g", Double.valueOf(g));
		coeffs.put("gamma", Double.valueOf(gamma));
		return coeffs;
	}

	@Override
	public Map<String, Double> getCoefficientsHint() {
		Map<String, Double> coeffs = new HashMap<String, Double>();
		coeffs.put("L", Double.valueOf(DEFAULT_L));
		coeffs.put("g", Double.valueOf(DEFAULT_G));
		coeffs.put("gamma", Double.valueOf(DEFAULT_GAMMA));
		return coeffs;
	}

	@Override
	public void setCoefficients(Map<String, Double> coefficients) {
		Double vv;

		vv = coefficients.get("L");
		double L2 = (vv == null) ? L : vv.doubleValue();

		vv = coefficients.get("g");
		double g2 = (vv == null) ? g : vv.doubleValue();

		vv = coefficients.get("gamma");
		double gamma2 = (vv == null) ? gamma : vv.doubleValue();

		setParams(L2, g2, gamma2);
	}

	@Override
	public void takeDerivatives(double t, double[] p, double[] dpdt) {
		innerODE.takeDerivatives(t, p, dpdt);
	}

	// ===========================
	// Private
	// ===========================

	private void setParams(double L, double g, double gamma) {
		this.L = L;
		this.g = g;
		this.gamma = gamma;

		this.minusGOverL = -g / L;
		this.innerODE = (gamma == 0) ? new PlanarCase() : new GeneralCase();
	}
}
