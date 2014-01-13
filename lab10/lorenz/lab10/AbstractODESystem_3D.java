package lorenz.lab10;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * @author jehanson
 */
public abstract class AbstractODESystem_3D implements ODESystem_3D {

	/**
	 * Default label for X coordinate. Equal to "x"
	 */
	public static final String X_LABEL = "x";

	/**
	 * Default label for Y coordinate. Equal to "y"
	 */
	public static final String Y_LABEL = "y";

	/**
	 * Default label for Z coordinate. Equal to "z"
	 */
	public static final String Z_LABEL = "z";
	
	/**
	 * This implementation returns {@link DataPoint.ZERO}
	 * 
	 * @see lorenz.lab10.ODESystem_3D#getInitialStateHint()
	 */
	@Override
	public DataPoint getInitialStateHint() {
		return DataPoint.ZERO;
	}

	/**
	 * This implementation returns { {@link X_LABEL}, {@link Y_LABEL}, {@link Z_LABEL} }
	 * 
	 * @see lorenz.lab10.ODESystem_3D#getCoordinateLabels()
	 */
	@Override
	public String[] getCoordinateLabels() {
		return new String[] { X_LABEL, Y_LABEL, Z_LABEL };
	}

	/**
	 * This implementation returns an empty map.
	 * 
	 * @see lorenz.lab10.ODESystem_3D#getCoefficients()
	 */
	@Override
	public Map<String, Double> getCoefficients() {
		return Collections.emptyMap();
	}

	/**
	 * This implementation returns an empty map.
	 * 
	 * @see lorenz.lab10.ODESystem_3D#getCoefficientsHint()
	 */
	@Override
	public Map<String, Double> getCoefficientsHint() {
		return Collections.emptyMap();
	}

	/**
	 * This implementation does nothing.
	 * 
	 * @see lorenz.lab10.ODESystem_3D#setCoefficients(java.util.Map)
	 */
	@Override
	public void setCoefficients(Map<String, Double> coefficients) {}

}
