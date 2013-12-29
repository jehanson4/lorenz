package lorenz.lab07;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
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

	private Control control;
	
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
		logger.info("setting up scenario");
		LorenzSystem sys = new LorenzSystem();
		DataPoint pt1 = sys.getInitialStateHint();
		DataSource ds1 = new RungeKutta4_3D("ds1", sys, pt1);
		DataPoint pt2 = new DataPoint(pt1.getX()+1E-3, pt1.getY(), pt1.getZ());
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
		logger.info("teardown");
		sources.remove("ds1");
		sources.remove("ds2");
		viewer.removeTimeseries("ds1");
		viewer.removeTimeseries("ds2");
	}
	
	@Override
	public Control buildControls(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		
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
