package lorenz.lab07;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author jehanson
 */
public class RandomWalkerScenario implements Scenario {

	private static final String clsName = RandomWalkerScenario.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ================================
	// Variables
	// ================================

	private final List<String> dsNames;
	
	// ================================
	// Creation
	// ================================

	public RandomWalkerScenario() {
		super();
		dsNames = new ArrayList<String>();
	}

	// ================================
	// Operation
	// ================================

	@Override
	public void setup(DataSourceContainer sources, Viewer viewer) {
		final String mtdName = "setup";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "enter");
		}

		DataSource ds1 = new RandomWalker();
		dsNames.add(ds1.getName());
		DataSource ds2 = new RandomWalker();
		dsNames.add(ds2.getName());
		
		sources.add(ds1);
		sources.add(ds2);

		viewer.setDataBounds(ds1.getDataBoundsHint());
		for (DataSource ds : sources.values()) {
			viewer.addTimeseries(ds);
		}

	}

	@Override
	public void teardown(DataSourceContainer sources, Viewer viewer) {
		for (String dsName : dsNames) {
			sources.remove(dsName);
			viewer.removeTimeseries(dsName);
		}
		dsNames.clear();
	}

	@Override
	public Control buildControls(Composite parent) {
		final String mtdName = "buildControls";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "no controls to build");
		}
		return null;
	}

	@Override
	public void dispose() {}

}
