package lorenz.lab10;

import java.util.Map;

/**
 * System of ordinary differential equations with 3 degrees of freedom.
 * 
 * @author jehanson
 */
public interface ODESystem_3D {

	public DataBox getDataBounds();

	public DataPoint getInitialStateHint();

	/**
	 * Returns labels for the coordinates of this ODESystem.
	 * @return length-3 array of non-null strings 
	 */
	String[] getCoordinateLabels();
	
	/**
	 * Returns the ODE system's coefficients in the form of key/value pairs.
	 * <p>
	 * No attempt is made to standardize the coefficient names. Each different
	 * ODE system will typically have its own idiosyncratic set of coefficients.
	 * 
	 * @return the mapping of coefficient names to values.
	 */
	public Map<String, Double> getCoefficients();

	public Map<String, Double> getCoefficientsHint();

	/**
	 * Reads values from the map to set coefficients.
	 * <p>
	 * Coefficients not named in the map are left unmodified. Unrecognized keys
	 * are ignored. Invalid values cause an exception to be thrown.
	 * 
	 * @param coefficients Map containing coefficients to be modified.
	 */
	public void setCoefficients(Map<String, Double> coefficients);

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

}