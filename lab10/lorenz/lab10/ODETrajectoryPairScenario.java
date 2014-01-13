package lorenz.lab10;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public abstract class ODETrajectoryPairScenario extends AbstractScenario {

	private static final String clsName = ODETrajectoryPairScenario.class.getName();
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
			if (key.equals(coordinateLabels[0])) {
				// should be safe b/c of field validator
				x = Double.parseDouble(value);
			}
			else if (key.equals(coordinateLabels[1])) {
				// should be safe b/c of field validator
				y = Double.parseDouble(value);
			}
			else if (key.equals(coordinateLabels[2])) {
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

			double dx = offset.getX();
			double dy = offset.getY();
			double dz = offset.getZ();
			if (key.equals(coordinateLabels[0])) {
				// should be safe b/c of field validator
				dx = Double.parseDouble(value);
			}
			else if (key.equals(coordinateLabels[1])) {
				// should be safe b/c of field validator
				dy = Double.parseDouble(value);
			}
			else if (key.equals(coordinateLabels[2])) {
				// should be safe b/c of field validator
				dz = Double.parseDouble(value);
			}

			offset = new DataPoint(dx, dy, dz);
			ds2.setInitialState(ds1.getInitialState().add(offset));

			if (logger.isLoggable(Level.FINE)) {
				String msg = "Offset is now " + offset;
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}
		}

	}

	private class SystemPropertyListener implements PropertyChangeListener {

		@Override
		public void propertyChanged(Object source, String key, String value) {
			final String mtdName = "SystemPropertyListener.propertyChanged";
			if (logger.isLoggable(Level.FINE)) {
				String msg = key + "=" + value;
				logger.logp(Level.FINE, clsName, mtdName, msg);
			}

			// should be safe b/c of field validators
			Double v = Double.valueOf(value);
			// Special handling b/c we added "timeStep" in buildControls
			if (key.equals("timeStep")) {
				setTimeStep(v.doubleValue());
				if (logger.isLoggable(Level.FINE)) {
					String msg = "timeStep is now " + getTimeStep();
					logger.logp(Level.FINE, clsName, mtdName, msg);
				}
			}
			else {
				Map<String, Double> coeffs = Collections.singletonMap(key, v);
				sys.setCoefficients(coeffs);
				if (logger.isLoggable(Level.FINE)) {
					String msg = "Coefficients are now " + sys.getCoefficients();
					logger.logp(Level.FINE, clsName, mtdName, msg);
				}
			}
		}
	}

	private class Legend extends ScenarioLegend {

		@Override
		public String[] getCoordinateLabels() {
			return coordinateLabels;
		}

		@Override
		public String getTimeLabel() {
			return "t=" + sources.getCurrentTime();
		}
	}

	// ================================
	// Variables
	// ================================

	private static final DataPoint OFFSET_DEFAULT = new DataPoint(0.01, 0.01, 0.01);
	private final ODESystem_3D sys;
	private final String[] coordinateLabels;

	private DataSourceContainer sources;
	private RungeKutta4_3D ds1;
	private RungeKutta4_3D ds2;
	private DataPoint offset;
	private Legend legend;
	private Composite cpane;
	private PropertySheet sysSheet;
	private PropertySheet icSheet;
	private PropertySheet offsetSheet;

	// ================================
	// Creation
	// ================================

	public ODETrajectoryPairScenario(ODESystem_3D sys) {
		super();
		if (sys == null)
			throw new IllegalArgumentException("Argument \"sys\" cannot be null");
		this.sys = sys;
		this.coordinateLabels = sys.getCoordinateLabels();
		ds1 = null;
		offset = OFFSET_DEFAULT;
		legend = new Legend();
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
		if (sources != null)
			sources.setTimeStep(x);
	}

	public DataPoint getIC() {
		return (ds1 == null) ? DataPoint.ZERO : ds1.getInitialState();
	}

	public DataPoint getICDefault() {
		return sys.getInitialStateHint();
	}

	public void setIC(DataPoint ic) {
		if (ds1 != null)
			ds1.setInitialState(ic);
		if (ds2 != null)
			ds2.setInitialState(ic.add(offset));
	}

	public String[] getCoordinateLabels() {
		return sys.getCoordinateLabels();
	}

	@Override
	public void setup(DataSourceContainer sources, Viewer viewer) {
		final String mtdName = "setup";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "enter");
		}

		this.sources = sources;

		DataPoint ic1 = sys.getInitialStateHint();
		ds1 = new RungeKutta4_3D("ds1", sys, ic1);
		DataPoint ic2 = ic1.add(this.offset);
		ds2 = new RungeKutta4_3D("ds2", sys, ic2);
		sources.add(ds1);
		sources.add(ds2);

		viewer.setDataBounds(sys.getDataBounds());
		viewer.addTimeseries(ds1);
		viewer.addTimeseries(ds2);

		legend = new Legend();
		viewer.addViewDecorator(legend);
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

		if (legend != null) {
			viewer.removeViewDecorator(legend);
			legend = null;
		}
	}

	@Override
	public Control buildControls(Composite parent) {
		final String mtdName = "buildControls";
		if (logger.isLoggable(Level.FINE))
			logger.logp(Level.FINE, clsName, mtdName, "enter");

		final PropertySheet.DoubleValidator anyDouble =
				PropertySheet.DoubleValidator.any();
		final PropertySheet.DoubleValidator posDouble =
				PropertySheet.DoubleValidator.greaterThan(0.);
		final int nCols = 4;

		cpane = new Composite(parent, SWT.NONE);
		cpane.setLayout(new GridLayout(nCols, false));

		// ------------------------------------

		Label sysLabel = new Label(cpane, SWT.BEGINNING);
		sysLabel.setText("System parameters");
		sysLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, nCols, 1));

		sysSheet = new PropertySheet();
		sysSheet.setFieldWidthHint(PropertySheet.NUMERIC_FIELD_WIDTH_HINT);
		sysSheet.addPropertyChangeListener(new SystemPropertyListener());
		for (Map.Entry<String, Double> entry : sys.getCoefficients().entrySet()) {
			sysSheet.addProperty(entry.getKey(), String.valueOf(entry.getValue()),
					getCoefficientValidator(entry.getKey()));
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
		icSheet.addProperty(coordinateLabels[0], String.valueOf(ic.getX()),
				getCoordinateValidator(coordinateLabels[0]));
		icSheet.addProperty(coordinateLabels[1], String.valueOf(ic.getY()),
				getCoordinateValidator(coordinateLabels[1]));
		icSheet.addProperty(coordinateLabels[2], String.valueOf(ic.getZ()),
				getCoordinateValidator(coordinateLabels[2]));

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
				icSheet.setProperty(coordinateLabels[0], String.valueOf(ic.getX()));
				icSheet.setProperty(coordinateLabels[1], String.valueOf(ic.getY()));
				icSheet.setProperty(coordinateLabels[2], String.valueOf(ic.getZ()));
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
		offsetSheet.addProperty(coordinateLabels[0], String.valueOf(offset.getX()),
				anyDouble);
		offsetSheet.addProperty(coordinateLabels[1], String.valueOf(offset.getY()),
				anyDouble);
		offsetSheet.addProperty(coordinateLabels[2], String.valueOf(offset.getZ()),
				anyDouble);

		Control offsetSheetControl = offsetSheet.buildControls(cpane);
		offsetSheetControl.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false,
				nCols, 1));

		Button offsetReset = new Button(cpane, SWT.PUSH);
		offsetReset.setText("Defaults");
		offsetReset.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, nCols,
				1));
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

	protected abstract PropertySheet.FieldValidator getCoefficientValidator(
			String coefficientLabel);

	protected abstract PropertySheet.FieldValidator getCoordinateValidator(
			String coordinateLabel);
}
