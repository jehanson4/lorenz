package lorenz.lab08;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import lorenz.lab08.PropertySheet.DoubleValidator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author jehanson
 */
public class LorenzScenario extends AbstractScenario {

	private static final String clsName = LorenzScenario.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ================================
	// Inner classes
	// ================================

	private class ICListener implements PropertyChangeListener {
		@Override
		public void propertyChanged(Object source, String key, String value) {
			final String mtdName = "ICListener.propertyChanged";
			if (logger.isLoggable(Level.FINE)) {
				logger.logp(Level.FINE, clsName, mtdName, key + "=" + value);
			}

			DataPoint ic1 = ds1.getInitialState();
			double x = ic1.getX();
			double y = ic1.getY();
			double z = ic1.getZ();
			if (key.equals("x")) {
				// should be safe b/c of field validator
				x = Double.parseDouble(value);
			}
			else if (key.equals("y")) {
				// should be safe b/c of field validator
				y = Double.parseDouble(value);
			}
			else if (key.equals("z")) {
				// should be safe b/c of field validator
				z = Double.parseDouble(value);
			}

			DataPoint ic = new DataPoint(x, y, z);
			ds1.setInitialState(ic);
			ds2.setInitialState(ic.add(offset));

			if (logger.isLoggable(Level.FINE)) {
				String msg = "IC is now " + ds1.getInitialState();
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}
		}
	}

	private class OffsetListener implements PropertyChangeListener {

		@Override
		public void propertyChanged(Object source, String key, String value) {
			final String mtdName = "OffsetListener.propertyChanged";
			if (logger.isLoggable(Level.FINE)) {
				logger.logp(Level.FINE, clsName, mtdName, key + "=" + value);
			}

			double x = offset.getX();
			double y = offset.getY();
			double z = offset.getZ();
			if (key.equals("x")) {
				// should be safe b/c of field validator
				x = Double.parseDouble(value);
			}
			else if (key.equals("y")) {
				// should be safe b/c of field validator
				y = Double.parseDouble(value);
			}
			else if (key.equals("z")) {
				// should be safe b/c of field validator
				z = Double.parseDouble(value);
			}

			offset = new DataPoint(x, y, z);
			ds2.setInitialState(ds1.getInitialState().add(offset));

			if (logger.isLoggable(Level.FINE)) {
				String msg = "Offset is now " + offset;
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}
		}

	}

	private class CoeffListener implements PropertyChangeListener {

		@Override
		public void propertyChanged(Object source, String key, String value) {
			final String mtdName = "CoeffListener.propertyChanged";
			if (logger.isLoggable(Level.FINE)) {
				String msg = key + "=" + value;
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}

			// should be safe b/c of field validator
			Double coeff = Double.valueOf(value);
			Map<String, Double> coeffs = Collections.singletonMap(key, coeff);
			sys.setCoefficients(coeffs);
			if (logger.isLoggable(Level.FINE)) {
				String msg = "Coefficients are now " + sys.getCoefficients();
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}
		}
	}

	// ================================
	// Variables
	// ================================

	private static final DataPoint OFFSET_DEFAULT = new DataPoint(1E-3, 0, 0);
	private final LorenzSystem sys;
	private RungeKutta4_3D ds1;
	private RungeKutta4_3D ds2;
	private DataPoint offset;
	private Composite cpane;
	private PropertySheet sysSheet;
	private PropertySheet icSheet;
	private PropertySheet offsetSheet;

	// ================================
	// Creation
	// ================================

	public LorenzScenario() {
		super();
		sys = new LorenzSystem();
		ds1 = null;
		offset = OFFSET_DEFAULT;
		cpane = null;
		sysSheet = null;
		icSheet = null;
		offsetSheet = null;
	}

	// ================================
	// Operation
	// ================================

	public double getTimeStep() {
		return (ds1 == null) ? 0 : ds1.getTimeStep();
	}

	public double getTimeStepDefault() {
		return (ds1 == null) ? 0 : ds1.getTimeStepDefault();
	}

	public void setTimeStep(double x) {
		if (ds1 != null)
			ds1.setTimeStep(x);
		if (ds2 != null)
			ds2.setTimeStep(x);
	}

	public DataPoint getIC() {
		return (ds1 == null) ? DataPoint.ORIGIN : ds1.getInitialState();
	}

	public DataPoint getICDefault() {
		return (sys == null) ? DataPoint.ORIGIN : sys.getInitialStateHint();
	}

	public void setIC(DataPoint ic) {
		if (ds1 != null)
			ds1.setInitialState(ic);
		if (ds2 != null)
			ds2.setInitialState(ic.add(offset));
	}

	@Override
	public void setup(DataSourceContainer sources, Viewer viewer) {
		final String mtdName = "setup";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "enter");
		}

		DataPoint ic1 = sys.getInitialStateHint();
		ds1 = new RungeKutta4_3D("ds1", sys, ic1);
		DataPoint ic2 = ic1.add(this.offset);
		ds2 = new RungeKutta4_3D("ds2", sys, ic2);
		sources.add(ds1);
		sources.add(ds2);

		viewer.setDataBounds(sys.getDataBoundsHint());
		viewer.addTimeseries(ds1);
		viewer.addTimeseries(ds2);
	}

	@Override
	public void teardown(DataSourceContainer sources, Viewer viewer) {
		final String mtdName = "teardown";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "enter");
		}
		if (ds1 != null) {
			sources.remove(ds1.getName());
			viewer.removeTimeseries(ds1.getName());
			ds1 = null;
		}
		if (ds2 != null) {
			sources.remove(ds2.getName());
			viewer.removeTimeseries(ds2.getName());
			ds2 = null;
		}
	}

	@Override
	public Control buildControls(Composite parent) {
		return buildControls2(parent);
	}

// public Control buildControls1(Composite parent) {
//
// final String mtdName = "buildControls";
// if (logger.isLoggable(Level.FINE))
// logger.logp(Level.FINE, clsName, mtdName, "enter");
//
// // Reused in multiple property sheets below.
// final PropertySheet.DoubleValidator anyDouble =
// new PropertySheet.DoubleValidator();
// final PropertySheet.DoubleValidator posDouble =
// PropertySheet.DoubleValidator.greaterThan(0.);
//
//
// bar = new ExpandBar(parent, SWT.V_SCROLL);
// bar.addExpandListener(new ExpandListener() {
// @Override
// public void itemCollapsed(ExpandEvent e) {}
//
// @Override
// public void itemExpanded(ExpandEvent e) {
// for (ExpandItem ii : bar.getItems()) {
// if (ii != e.item) {
// ii.setExpanded(false);
// }
// }
// }
// });
//
// // ------------------------------------
//
// sysSheet = new PropertySheet();
// sysSheet.setFieldWidthHint(PropertySheet.NUMERIC_FIELD_WIDTH_HINT);
// sysSheet.addPropertyChangeListener(new CoeffListener());
// for (Map.Entry<String, Double> entry : sys.getCoefficients().entrySet()) {
// sysSheet.addProperty(entry.getKey(), String.valueOf(entry.getValue()),
// anyDouble);
// sysSheet.addProperty("timeStep", String.valueOf(getTimeStep()), posDouble);
// }
//
// Composite coeffPane = new Composite(bar, SWT.NONE);
// coeffPane.setLayout(new GridLayout(2, false));
// Control coeffControl = sysSheet.buildControls(coeffPane);
// coeffControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, 2,
// 1));
// Button coeffReset = new Button(coeffPane, SWT.PUSH);
// coeffReset.setText("Reset to Defaults");
// coeffReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 2,
// 1));
// coeffReset.addSelectionListener(new SelectionAdapter() {
// @Override
// public void widgetSelected(SelectionEvent e) {
// Map<String, Double> newCoeffs = sys.getCoefficientsHint();
// sys.setCoefficients(newCoeffs);
// for (Map.Entry<String, Double> entry : newCoeffs.entrySet()) {
// sysSheet.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
// }
// }
// });
// ExpandItem coeffItem = new ExpandItem(bar, SWT.NONE);
// coeffItem.setText("Lorenz System Coefficients");
// coeffItem.setHeight(coeffPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
// coeffItem.setControl(coeffPane);
//
// // ------------------------------------
//
// final DataPoint ic = ds1.getInitialState();
// icSheet = new PropertySheet();
// icSheet.setFieldWidthHint(PropertySheet.NUMERIC_FIELD_WIDTH_HINT);
// icSheet.addPropertyChangeListener(new ICListener());
// icSheet.addProperty("x", String.valueOf(ic.getX()), anyDouble);
// icSheet.addProperty("y", String.valueOf(ic.getY()), anyDouble);
// icSheet.addProperty("z", String.valueOf(ic.getZ()), anyDouble);
//
// Composite icPane = new Composite(bar, SWT.NONE);
// icPane.setLayout(new GridLayout(2, false));
// Control icControl = icSheet.buildControls(icPane);
// icControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, 2, 1));
// Button icReset = new Button(icPane, SWT.PUSH);
// icReset.setText("Reset to Defaults");
// icReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 2, 1));
// icReset.addSelectionListener(new SelectionAdapter() {
// @Override
// public void widgetSelected(SelectionEvent e) {
// DataPoint ic = sys.getInitialStateHint();
// ds1.setInitialState(ic);
// ds2.setInitialState(ic.add(offset));
// icSheet.setProperty("x", String.valueOf(ic.getX()));
// icSheet.setProperty("y", String.valueOf(ic.getY()));
// icSheet.setProperty("z", String.valueOf(ic.getZ()));
// }
// });
// ExpandItem icItem = new ExpandItem(bar, SWT.NONE);
// icItem.setText("Initial Conditions");
// icItem.setHeight(icPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
// icItem.setControl(icPane);
//
// // ------------------------------------
//
// offsetSheet = new PropertySheet();
// offsetSheet.setFieldWidthHint(PropertySheet.NUMERIC_FIELD_WIDTH_HINT);
// offsetSheet.addPropertyChangeListener(new OffsetListener());
// offsetSheet.addProperty("x", String.valueOf(offset.getX()), anyDouble);
// offsetSheet.addProperty("y", String.valueOf(offset.getY()), anyDouble);
// offsetSheet.addProperty("z", String.valueOf(offset.getZ()), anyDouble);
//
// Composite offsetPane = new Composite(bar, SWT.NONE);
// offsetPane.setLayout(new GridLayout(2, false));
// Control offsetControl = offsetSheet.buildControls(offsetPane);
// offsetControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, 2,
// 1));
// Button offsetReset = new Button(offsetPane, SWT.PUSH);
// offsetReset.setText("Reset to Defaults");
// offsetReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 2,
// 1));
// offsetReset.addSelectionListener(new SelectionAdapter() {
// @Override
// public void widgetSelected(SelectionEvent e) {
// offset = DEFAULT_OFFSET;
// ds2.setInitialState(ds1.getInitialState().add(offset));
// offsetSheet.setProperty("x", String.valueOf(offset.getX()));
// offsetSheet.setProperty("y", String.valueOf(offset.getY()));
// offsetSheet.setProperty("z", String.valueOf(offset.getZ()));
// }
// });
// ExpandItem offsetItem = new ExpandItem(bar, SWT.NONE);
// offsetItem.setText("Initial offset in 2nd timseries");
// offsetItem.setHeight(offsetPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
// offsetItem.setControl(offsetPane);
//
// // ------------------------------------
//
// return bar;
// }

	public Control buildControls2(Composite parent) {

		final String mtdName = "buildControls";
		if (logger.isLoggable(Level.FINE))
			logger.logp(Level.FINE, clsName, mtdName, "enter");

		// reused in multiple property sheets below.
		final DoubleValidator anyDouble = DoubleValidator.any();
		final DoubleValidator posDouble = DoubleValidator.greaterThan(0.);
		final int nCols = 4;

		cpane = new Composite(parent, SWT.NONE);
		cpane.setLayout(new GridLayout(nCols, false));

		// ------------------------------------

		Label sysLabel = new Label(cpane, SWT.BEGINNING);
		sysLabel.setText("System parameters");
		sysLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, nCols, 1));

		sysSheet = new PropertySheet();
		sysSheet.setFieldWidthHint(PropertySheet.NUMERIC_FIELD_WIDTH_HINT);
		sysSheet.addPropertyChangeListener(new CoeffListener());
		for (Map.Entry<String, Double> entry : sys.getCoefficients().entrySet()) {
			sysSheet.addProperty(entry.getKey(), String.valueOf(entry.getValue()),
					anyDouble);
		}
		sysSheet.addProperty("timeStep", String.valueOf(getTimeStep()), posDouble);

		Control sysSheetControl = sysSheet.buildControls(cpane);
		sysSheetControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, nCols,
				1));

		Button sysReset = new Button(cpane, SWT.PUSH);
		sysReset.setText("Defaults");
		sysReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, nCols, 1));
		sysReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Map<String, Double> newCoeffs = sys.getCoefficientsHint();
				sys.setCoefficients(newCoeffs);
				for (Map.Entry<String, Double> entry : newCoeffs.entrySet()) {
					sysSheet.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
				}
				sysSheet.setProperty("timeStep", String.valueOf(getTimeStepDefault()));
			}
		});

		// ------------------------------------

		Label icLabel = new Label(cpane, SWT.BEGINNING);
		icLabel.setText("Initial Condition");
		icLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, nCols, 1));

		final DataPoint ic = ds1.getInitialState();
		icSheet = new PropertySheet();
		icSheet.setFieldWidthHint(PropertySheet.NUMERIC_FIELD_WIDTH_HINT);
		icSheet.addPropertyChangeListener(new ICListener());
		icSheet.addProperty("x", String.valueOf(ic.getX()), anyDouble);
		icSheet.addProperty("y", String.valueOf(ic.getY()), anyDouble);
		icSheet.addProperty("z", String.valueOf(ic.getZ()), anyDouble);

		Control icSheetControl = icSheet.buildControls(cpane);
		icSheetControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, nCols,
				1));

		Button icReset = new Button(cpane, SWT.PUSH);
		icReset.setText("Defaults");
		icReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, nCols, 1));
		icReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataPoint ic = getICDefault();
				setIC(ic);
				icSheet.setProperty("x", String.valueOf(ic.getX()));
				icSheet.setProperty("y", String.valueOf(ic.getY()));
				icSheet.setProperty("z", String.valueOf(ic.getZ()));
			}
		});

		// ------------------------------------

		Label offsetLabel = new Label(cpane, SWT.BEGINNING);
		offsetLabel.setText("Initial offset in 2nd timeseries");
		offsetLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, nCols,
				1));

		offsetSheet = new PropertySheet();
		offsetSheet.setFieldWidthHint(PropertySheet.NUMERIC_FIELD_WIDTH_HINT);
		offsetSheet.addPropertyChangeListener(new OffsetListener());
		offsetSheet.addProperty("dx", String.valueOf(offset.getX()), anyDouble);
		offsetSheet.addProperty("dy", String.valueOf(offset.getY()), anyDouble);
		offsetSheet.addProperty("dz", String.valueOf(offset.getZ()), anyDouble);

		Control offsetSheetControl = offsetSheet.buildControls(cpane);
		offsetSheetControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false,
				nCols, 1));

		Button offsetReset = new Button(cpane, SWT.PUSH);
		offsetReset.setText("Defaults");
		offsetReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, nCols, 1));
		offsetReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				offset = OFFSET_DEFAULT;
				ds2.setInitialState(ds1.getInitialState().add(offset));
				offsetSheet.setProperty("dx", String.valueOf(offset.getX()));
				offsetSheet.setProperty("dy", String.valueOf(offset.getY()));
				offsetSheet.setProperty("dz", String.valueOf(offset.getZ()));
			}
		});

		// ------------------------------------

		return cpane;
	}

	@Override
	public void dispose() {
		if (cpane != null) {
			cpane.dispose();
			cpane = null;
		}
	}

}
