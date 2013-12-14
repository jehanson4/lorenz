package lorenz.lab05;

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

	private double sigma;
	private double rho;
	private double beta;

	// ==================================
	// Creation
	// ==================================

	public LorenzSystem() {
		super();
		// Pick the usual values.
		sigma = 10;
		rho = 28;
		beta = 8.0 / 3.0;
	}

	// ==================================
	// Operation
	// ==================================

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
