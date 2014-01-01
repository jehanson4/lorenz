package lorenz.lab08;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author jehanson
 */
public class RandomWalkScenario extends AbstractScenario {

	private static final String clsName = RandomWalkScenario.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ================================
	// Variables
	// ================================

	private final List<String> dsNames;
	private Composite control;

	// ================================
	// Creation
	// ================================

	public RandomWalkScenario() {
		super();
		dsNames = new ArrayList<String>();
		control = null;
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
