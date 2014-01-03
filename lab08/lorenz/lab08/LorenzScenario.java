package lorenz.lab08;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
	// Inner classes
	// ================================

	private class DoubleValidator implements PropertySheet.FieldValidator {

		@Override
		public boolean isValid(String s) {
			try {
				Double.parseDouble(s);
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}
	}

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

	private static final DataPoint DEFAULT_OFFSET = new DataPoint(1E-3, 0, 0);
	private static final int NUMERIC_FIELD_WIDTH = 80;

	private final LorenzSystem sys;
	private RungeKutta4_3D ds1;
	private RungeKutta4_3D ds2;
	private DataPoint offset;
	private ExpandBar bar;
	private PropertySheet coeffSheet;
	private PropertySheet icSheet;
	private PropertySheet offsetSheet;

	// ================================
	// Creation
	// ================================

	public LorenzScenario() {
		super();
		sys = new LorenzSystem();
		ds1 = null;
		offset = DEFAULT_OFFSET;
		bar = null;
		coeffSheet = null;
		icSheet = null;
		offsetSheet = null;
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

		// Reused in multiple property sheets below.
		final DoubleValidator dval = new DoubleValidator();

		// ------------------------------------

		coeffSheet = new PropertySheet();
		coeffSheet.setFieldWidthHint(NUMERIC_FIELD_WIDTH);
		coeffSheet.addPropertyChangeListener(new CoeffListener());
		for (Map.Entry<String, Double> entry : sys.getCoefficients().entrySet()) {
			coeffSheet
					.addProperty(entry.getKey(), String.valueOf(entry.getValue()), dval);
		}

		Composite coeffPane = new Composite(bar, SWT.NONE);
		coeffPane.setLayout(new GridLayout(2, false));
		Control coeffControl = coeffSheet.buildControls(coeffPane);
		coeffControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, 2, 1));
		Button coeffReset = new Button(coeffPane, SWT.PUSH);
		coeffReset.setText("Reset to Defaults");
		coeffReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 2, 1));
		coeffReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Map<String, Double> newCoeffs = sys.getCoefficientsHint();
				sys.setCoefficients(newCoeffs);
				for (Map.Entry<String, Double> entry : newCoeffs.entrySet()) {
					coeffSheet.setProperty(entry.getKey(),
							String.valueOf(entry.getValue()));
				}
			}
		});
		ExpandItem coeffItem = new ExpandItem(bar, SWT.NONE);
		coeffItem.setText("Lorenz System Coefficients");
		coeffItem.setHeight(coeffPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		coeffItem.setControl(coeffPane);

		// ------------------------------------

		final DataPoint ic = ds1.getInitialState();
		icSheet = new PropertySheet();
		icSheet.setFieldWidthHint(NUMERIC_FIELD_WIDTH);
		icSheet.addPropertyChangeListener(new ICListener());
		icSheet.addProperty("x", String.valueOf(ic.getX()), dval);
		icSheet.addProperty("y", String.valueOf(ic.getY()), dval);
		icSheet.addProperty("z", String.valueOf(ic.getZ()), dval);

		Composite icPane = new Composite(bar, SWT.NONE);
		icPane.setLayout(new GridLayout(2, false));
		Control icControl = icSheet.buildControls(icPane);
		icControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, 2, 1));
		Button icReset = new Button(icPane, SWT.PUSH);
		icReset.setText("Reset to Defaults");
		icReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 2, 1));
		icReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataPoint ic = sys.getInitialStateHint();
				ds1.setInitialState(ic);
				ds2.setInitialState(ic.add(offset));
				icSheet.setProperty("x", String.valueOf(ic.getX()));
				icSheet.setProperty("y", String.valueOf(ic.getY()));
				icSheet.setProperty("z", String.valueOf(ic.getZ()));
			}
		});
		ExpandItem icItem = new ExpandItem(bar, SWT.NONE);
		icItem.setText("Initial Conditions");
		icItem.setHeight(icPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		icItem.setControl(icPane);

		// ------------------------------------

		offsetSheet = new PropertySheet();
		offsetSheet.setFieldWidthHint(NUMERIC_FIELD_WIDTH);
		offsetSheet.addPropertyChangeListener(new OffsetListener());
		offsetSheet.addProperty("x", String.valueOf(offset.getX()), dval);
		offsetSheet.addProperty("y", String.valueOf(offset.getY()), dval);
		offsetSheet.addProperty("z", String.valueOf(offset.getZ()), dval);

		Composite offsetPane = new Composite(bar, SWT.NONE);
		offsetPane.setLayout(new GridLayout(2, false));
		Control offsetControl = offsetSheet.buildControls(offsetPane);
		offsetControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false, 2, 1));
		Button offsetReset = new Button(offsetPane, SWT.PUSH);
		offsetReset.setText("Reset to Defaults");
		offsetReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 2, 1));
		offsetReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				offset = DEFAULT_OFFSET;
				ds2.setInitialState(ds1.getInitialState().add(offset));
				offsetSheet.setProperty("x", String.valueOf(offset.getX()));
				offsetSheet.setProperty("y", String.valueOf(offset.getY()));
				offsetSheet.setProperty("z", String.valueOf(offset.getZ()));
			}
		});
		ExpandItem offsetItem = new ExpandItem(bar, SWT.NONE);
		offsetItem.setText("Initial offset in 2nd timseries");
		offsetItem.setHeight(offsetPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		offsetItem.setControl(offsetPane);

		// ------------------------------------

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
