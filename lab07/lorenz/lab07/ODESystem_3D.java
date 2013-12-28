package lorenz.lab07;

import java.util.Properties;

/**
 * System of ordinary differential equations with 3 degrees of freedom.
 * 
 * @author jehanson
 */
public interface ODESystem_3D {

	public DataBox getDataBoundsHint();

	public DataPoint getInitialStateHint();

	/**
	 * Calculates the derivatives with respect to time, evaluated at the given
	 * point.
	 * <p>
	 * Because this method is called so frequently, it signature is tuned for
	 * efficiency -- i.e, raw arrays, assumed already allocated, are used.
	 * 
	 * @param t
	 *            The time at which the derivatives are calculated. Must be a
	 *            non-null array of length >= 3.
	 * @param p
	 *            The point at which the derivatives are calculated
	 * @param dpdt
	 *            The array into which the values of the derivatives are placed.
	 *            Must be a non-null array of length >= 3.
	 */
	public void takeDerivatives(double t, double[] p, double[] dpdt);

	/**
	 * Attempts to set the receiver's parameters from the given Properties
	 * object. Unrecognized parameter names and illegal values are ignored.
	 * Parameters not found in the given Properties object are left unchanged.
	 */
	public abstract void setParameters(Properties params);

	/**
	 * Returns the receiver's default parameters in the form of a Properties
	 * object.
	 * 
	 * @return the default parameters. Not null, but may be empty.
	 */
	public abstract Properties getParameterDefaults();

	/**
	 * Returns the receiver's current parameters in the form of a Properties
	 * object.
	 * 
	 * @return the current parameters. Not null, but may be empty.
	 */
	public Properties getParameters();
	
}