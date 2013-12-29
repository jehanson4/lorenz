package lorenz.lab07;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author jehanson
 */
public class RandomWalkScenario implements Scenario {

	private static final String clsName = RandomWalkScenario.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ================================
	// Variables
	// ================================

	private Composite control;

	// ================================
	// Creation
	// ================================

	public RandomWalkScenario() {
		super();
		control = null;
	}

	// ================================
	// Operation
	// ================================

	/*
	 * (non-Javadoc)
	 * 
	 * @see lorenz.lab07.Scenario#setup(lorenz.lab07.DataSourceContainer,
	 * lorenz.lab07.Viewer)
	 */
	@Override
	public void setup(DataSourceContainer sources, Viewer viewer) {
		final String mtdName = "setup";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "enter");
		}
		
		sources.reset();

		DataSource ds1 = new RandomWalker("ds1");
		DataSource ds2 = new RandomWalker("ds2");

		sources.add(ds1);
		sources.add(ds2);

		viewer.setDataBounds(ds1.getDataBoundsHint());
		for (DataSource ds : sources.values()) {
			viewer.addTimeseries(ds);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lorenz.lab07.Scenario#teardown(lorenz.lab07.DataSourceContainer,
	 * lorenz.lab07.Viewer)
	 */
	@Override
	public void teardown(DataSourceContainer sources, Viewer viewer) {
		sources.remove("ds1");
		sources.remove("ds2");
		viewer.removeTimeseries("ds1");
		viewer.removeTimeseries("ds2");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lorenz.lab07.Scenario#buildControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control buildControls(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		// TODO
		return control;
	}

	@Override
	public void dispose() {
		if (control != null) {
			control.dispose();
			control = null;
		}
	}

}
