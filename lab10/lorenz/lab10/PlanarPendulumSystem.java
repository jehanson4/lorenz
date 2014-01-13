package lorenz.lab10;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 3-D pendulum with zero rotational speed (i.e., <code>d(phi)/dt == 0</code>).
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
 * dpdt[2] = -g / L * sin(p[0]);
 * </pre>
 * 
 * @author jehanson
 */
public class PlanarPendulumSystem extends AbstractODESystem_3D {

	// =====================================
	// Variables
	// =====================================

	public static final String THETA_LABEL = "\u03B8";
	public static final String PHI_LABEL = "\u03C6";
	public static final String THETA_DOT_LABEL = "d\u03B8/dt";

	public static final String L_LABEL = "L";
	public static final String G_LABEL = "g";

	private static final double L_DEFAULT = 1;
	private static final double G_DEFAULT = 9.8;

	private static final DataPoint IC_DEFAULT = new DataPoint(7. * Math.PI / 8., 0, 0);

	private double L;	// length of pendulum's arm
	private double g;	// gravitational acceleration
	private double minusGOverL;

	// =====================================
	// Creation
	// =====================================

	public PlanarPendulumSystem() {
		super();
		setParams(L_DEFAULT, G_DEFAULT);
	}

	// =====================================
	// Operation
	// =====================================

	@Override
	public String[] getCoordinateLabels() {
		return new String[] { THETA_LABEL, PHI_LABEL, THETA_DOT_LABEL };
	}

	
	@Override
	public DataBox getDataBounds() {
		// x = theta => [0, PI]
		// y = phi => [0, 2*PI]
		// z = thetaDot => [-10, 10] ? . . . just guessing here . . .
		// plus some margin . . .
		double xMin = -Math.PI;
		double xMax = Math.PI;
		DataBox.BoundType xType = DataBox.BoundType.PERIODIC;
		double yMin = 0;
		double yMax = 2 * Math.PI;
		DataBox.BoundType yType = DataBox.BoundType.PERIODIC;
		double zMin = -10;
		double zMax = 10;
		DataBox.BoundType zType = DataBox.BoundType.PERMEABLE;
		return new DataBox(xMin, xMax, xType, yMin, yMax, yType, zMin, zMax, zType);
	}

	@Override
	public DataPoint getInitialStateHint() {
		return IC_DEFAULT;
	}

	@Override
	public Map<String, Double> getCoefficients() {
		Map<String, Double> coeffs = new LinkedHashMap<String, Double>();
		coeffs.put(L_LABEL, Double.valueOf(L));
		coeffs.put(G_LABEL, Double.valueOf(g));
		return coeffs;
	}

	@Override
	public Map<String, Double> getCoefficientsHint() {
		Map<String, Double> coeffs = new LinkedHashMap<String, Double>();
		coeffs.put(L_LABEL, Double.valueOf(L_DEFAULT));
		coeffs.put(G_LABEL, Double.valueOf(G_DEFAULT));
		return coeffs;
	}

	@Override
	public void setCoefficients(Map<String, Double> coefficients) {
		Double vv;

		vv = coefficients.get(L_LABEL);
		double L2 = (vv == null) ? L : vv.doubleValue();

		vv = coefficients.get(G_LABEL);
		double g2 = (vv == null) ? g : vv.doubleValue();

		setParams(L2, g2);
	}

	@Override
	public void takeDerivatives(double t, double[] p, double[] dpdt) {
		dpdt[0] = p[2];
		dpdt[1] = 0;
		dpdt[2] = minusGOverL * Math.sin(p[0]);
	}

	// ===========================
	// Private
	// ===========================

	private void setParams(double L, double g) {
		this.L = L;
		this.g = g;
		this.minusGOverL = -g / L;
	}
}
