package lorenz.lab08;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

/**
 * 
 * @author jehanson
 */
public class LorenzScenario extends AbstractScenario {

	private static final String clsName = LorenzScenario.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ================================
	// Variables
	// ================================

	private final LorenzSystem sys;
	private final List<String> dsNames;
	private DataPointProvider initialCondition;
	private DataPointProvider secondTimeseriesOffset;
	private ExpandBar bar;

	// ================================
	// Creation
	// ================================

	public LorenzScenario() {
		super();
		sys = new LorenzSystem();
		dsNames = new ArrayList<String>();
		initialCondition = new DataPointProvider(sys.getInitialStateHint());
		secondTimeseriesOffset = new DataPointProvider(new DataPoint(1.E-2, 0., 0.));
		bar = null;
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

		DataSource ds1 = new RungeKutta4_3D("ds1", sys, initialCondition.getPoint());
		dsNames.add(ds1.getName());
		DataSource ds2 =
				new RungeKutta4_3D("ds2", sys,
						initialCondition.getPoint().add(this.secondTimeseriesOffset.getPoint()));
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
			logger.logp(Level.FINE, clsName, mtdName, "enter");

		bar = new ExpandBar(parent, SWT.V_SCROLL);
		bar.addExpandListener(new ExpandListener() {
			@Override
			public void itemCollapsed(ExpandEvent e) {}

			@Override
			public void itemExpanded(ExpandEvent e) {
				for (ExpandItem ii : bar.getItems()) {
					if (ii != e.item) {
						ii.setExpanded(false);
					}
				}
			}
		});

		// Q: memory leak???
		ParameterSheet coeffSheet = new ParameterSheet(sys);
		Control coeffControl = coeffSheet.buildControls(bar);
		ExpandItem coeffItem = new ExpandItem(bar, SWT.NONE);
		coeffItem.setText("System coefficients");
		coeffItem.setHeight(coeffControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		coeffItem.setControl(coeffControl);

		ParameterSheet icSheet = new ParameterSheet(initialCondition);
		Control icControl = icSheet.buildControls(bar);
		ExpandItem icItem = new ExpandItem(bar, SWT.NONE);
		icItem.setText("Initial condition");
		icItem.setHeight(icControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		icItem.setControl(icControl);

		ParameterSheet ts2Sheet = new ParameterSheet(secondTimeseriesOffset);
		Control ts2Control = ts2Sheet.buildControls(bar);
		ExpandItem ts2Item = new ExpandItem(bar, SWT.NONE);
		ts2Item.setText("Initial offset in 2nd timeseries");
		ts2Item.setHeight(ts2Control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		ts2Item.setControl(ts2Control);

		return bar;
	}

	@Override
	public void dispose() {
		if (bar != null) {
			bar.dispose();
			bar = null;
		}
	}

}
