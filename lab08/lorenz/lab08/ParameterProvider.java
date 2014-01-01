package lorenz.lab08;

import java.util.Properties;

/**
 * 
 * @author jehanson
 */
public interface ParameterProvider {
	
	/**
	 * Attempts to set the receiver's parameters to match the given Properties
	 * object. Unrecognized property names and illegal values are ignored.
	 * Properties not found in the given Properties object are left unchanged.
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
