package lorenz.lab09;

import java.util.logging.Level;
import java.util.logging.Logger;

import lorenz.lab09.PropertySheet.BoundedDoubleValidator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author jehanson
 */
public class RandomWalkerScenario extends AbstractScenario {

	private static final String clsName = RandomWalkerScenario.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ================================
	// Variables
	// ================================

	private RandomWalker ds1;
	private RandomWalker ds2;
	private Composite control;

	// ================================
	// Creation
	// ================================

	public RandomWalkerScenario() {
		super();
		ds1 = null;
		ds2 = null;
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

		ds1 = new RandomWalker();
		ds2 = new RandomWalker();

		sources.add(ds1);
		sources.add(ds2);

		viewer.setDataBounds(ds1.getDataBoundsHint());
		viewer.addTimeseries(ds1);
		viewer.addTimeseries(ds2);
	}

	@Override
	public void teardown(DataSourceContainer sources, Viewer viewer) {
		if (ds1 != null) {
			String dsName = ds1.getName();
			sources.remove(dsName);
			viewer.removeTimeseries(dsName);
			ds1 = null;
		}
		if (ds2 != null) {
			String dsName = ds2.getName();
			sources.remove(dsName);
			viewer.removeTimeseries(dsName);
			ds2 = null;
		}
	}

	@Override
	public Control buildControls(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));

		PropertySheet sheet = new PropertySheet();
		sheet.setFieldWidthHint(PropertySheet.NUMERIC_FIELD_WIDTH_HINT);
		Control sheetControl = sheet.buildControls(control);
		sheetControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, 2, 1));

		final BoundedDoubleValidator dval = BoundedDoubleValidator.greaterThan(0.);
		sheet.addProperty("stepSize", String.valueOf(ds1.getStepSize()), dval);
		sheet.addProperty("timeStep", String.valueOf(ds1.getTimeStep()), dval);
		sheet.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChanged(Object source, String key, String value) {
				final String mtdName = "propertyChanged";
				if (logger.isLoggable(Level.FINE)) {
					String msg = key + "=" + value;
					logger.logp(Level.FINE, clsName, mtdName, msg);
				}
				if (key.equals("stepSize")) {
					// should be safe b/c of validator
					double x = Double.parseDouble(value);
					ds1.setStepSize(x);
					ds2.setStepSize(x);
				}
				else if (key.equals("timeStep")) {
					// should be safe b/c of validator
					double x = Double.parseDouble(value);
					ds1.setTimeStep(x);
					ds2.setTimeStep(x);
				}
			}
		});

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
