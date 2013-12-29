package lorenz.lab07;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

	private Composite control;

	// ================================
	// Creation
	// ================================

	public LorenzScenario() {
		super();
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
		
		sources.reset();

		LorenzSystem sys = new LorenzSystem();
		DataPoint pt1 = sys.getInitialStateHint();
		DataSource ds1 = new RungeKutta4_3D("ds1", sys, pt1);
		DataPoint pt2 = new DataPoint(pt1.getX() + 1E-3, pt1.getY(), pt1.getZ());
		DataSource ds2 = new RungeKutta4_3D("ds2", sys, pt2);

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
		sources.remove("ds1");
		sources.remove("ds2");
		viewer.removeTimeseries("ds1");
		viewer.removeTimeseries("ds2");
	}

	@Override
	public Control buildControls(Composite parent) {
		final String mtdName = "buildControls";
		if (logger.isLoggable(Level.FINE))
			logger.logp(Level.FINE, clsName, mtdName, "enter");
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(1, false));
		
		Button b4 = new Button(control, SWT.CHECK);
		b4.setText("2nd timeseries");
		b4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
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
