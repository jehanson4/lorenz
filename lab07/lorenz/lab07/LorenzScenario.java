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
public class LorenzScenario implements Scenario {

	private static final String clsName = LorenzScenario.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ================================
	// Variables
	// ================================

	private final List<String> dsNames;
	
	// ================================
	// Creation
	// ================================

	public LorenzScenario() {
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
		
		LorenzSystem sys = new LorenzSystem();
		DataPoint pt1 = sys.getInitialStateHint();
		DataSource ds1 = new RungeKutta4_3D("ds1", sys, pt1);
		dsNames.add(ds1.getName());
		DataPoint pt2 = new DataPoint(pt1.getX()+1E-4, pt1.getY(), pt1.getZ());
		DataSource ds2 = new RungeKutta4_3D("ds2", sys, pt2);
		dsNames.add(ds2.getName());

		sources.add(ds1);
		sources.add(ds2);

		viewer.setDataBounds(sys.getDataBoundsHint());
		for (DataSource ds : sources.values()) {
			viewer.addTimeseries(ds);
		}
	}

	@Override
	public void teardown(DataSourceContainer sources, Viewer viewer) {
		final String mtdName = "teardown";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "enter");
		}
		for (String dsName : dsNames) {
			sources.remove(dsName);
			viewer.removeTimeseries(dsName);
		}
		dsNames.clear();
	}

	@Override
	public Control buildControls(Composite parent) {
		final String mtdName = "buildControls";
		if (logger.isLoggable(Level.FINE))
			logger.logp(Level.FINE, clsName, mtdName, "no controls to build");
		return null;
	}

	@Override
	public void dispose() {
	}
}
