package lorenz.lab07;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author jehanson
 */
public interface Scenario {

	public abstract void dispose();

	public abstract Control buildControls(Composite parent);

	public abstract void teardown(DataSourceContainer sources, Viewer viewer);

	public abstract void setup(DataSourceContainer sources, Viewer viewer);

}
