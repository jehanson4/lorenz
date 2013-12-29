package lorenz.lab07;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author jehanson
 */
public interface Scenario {

	/**
	 * Builds UI controls for this scenario's configurable parameters, if any.
	 * If no controls are defined, returns null.
	 * 
	 * @param parent
	 *            The parent of the root UI control built by this method
	 * @return the root UI control, or null
	 */
	public abstract Control buildControls(Composite parent);

	/**
	 * Disposes UI resources that were created in buildControls
	 */
	public abstract void dispose();

	/**
	 * Sets up this scenario by adding and configuring data sources and viewer
	 * timeseries.
	 * 
	 * @param sources
	 * @param viewer
	 */
	public abstract void setup(DataSourceContainer sources, Viewer viewer);

	/**
	 * Undoes the effect of setup.
	 * 
	 * @param sources
	 * @param viewer
	 */
	public abstract void teardown(DataSourceContainer sources, Viewer viewer);

}
