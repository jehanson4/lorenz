package lorenz.lab08;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author jehanson
 */
public class ParameterControl {

	private static final String clsName = ParameterControl.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ==========================
	// Inner class
	// ==========================

	private class FieldListener implements SelectionListener, FocusListener {

		private final String key;
		private final Text field;

		public FieldListener(String key, Text field) {
			super();
			this.key = key;
			this.field = field;
			field.addSelectionListener(this);
			field.addFocusListener(this);
		}

		@Override
		public void focusGained(FocusEvent e) {
			update();
		}

		@Override
		public void focusLost(FocusEvent e) {
			apply();
		}

		@Override
		public void widgetSelected(SelectionEvent e) {}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			apply();
		}

		public void apply() {
			logger.info("applying: key=" + key + " value=" + field.getText());
			// INEFFICIENT. So sue me.
			Properties pp = new Properties();
			pp.setProperty(String.valueOf(key), field.getText());
			provider.setParameters(pp);
			update();

		}

		public void update() {
			logger.info("updating: key=" + key + " value="
					+ provider.getParameters().get(key));
			// INEFFICIENT. So sue me.
			field.setText(String.valueOf(provider.getParameters().get(key)));
		}

		// public final void enable() {
		// field.addSelectionListener(this);
		// field.addFocusListener(this);
		// }
		//
		// public final void disable() {
		// field.removeSelectionListener(this);
		// field.removeFocusListener(this);
		// }
	}

	// ==========================
	// Variables
	// ==========================

	private ParameterProvider provider;
	private Composite cpane;

	// ==========================
	// Creation
	// ==========================

	public ParameterControl(ParameterProvider provider) {
		super();
		this.provider = provider;
		this.cpane = null;
	}

	// ==========================
	// Operation
	// ==========================

	public Control buildControls(Composite parent) {
		if (cpane != null)
			throw new IllegalStateException("Controls already built.");

		final int nCols = 3;

		cpane = new Composite(parent, SWT.NONE);
		cpane.setLayout(new GridLayout(nCols, false));

		Properties params = provider.getParameters();
		final List<FieldListener> fieldListeners = new ArrayList<FieldListener>();
		for (Map.Entry<?, ?> entry : params.entrySet()) {
			String k = String.valueOf(entry.getKey());
			String v = String.valueOf(entry.getValue());

			Label label = new Label(cpane, SWT.RIGHT);
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, nCols - 1,
					1));
			label.setText(k + " = ");
			Text text = new Text(cpane, SWT.SINGLE);
			text.setText(String.valueOf(v));
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

			FieldListener ff = new FieldListener(k, text);
			fieldListeners.add(ff);
		}

		Label blank = new Label(cpane, SWT.NONE);
		blank.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, nCols - 1, 1));

		Button defaultsButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		defaultsButton.setText("Restore Defaults");
		defaultsButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1,
				1));
		defaultsButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Properties p = provider.getParameterDefaults();
				provider.setParameters(p);
				for (FieldListener f : fieldListeners) {
					f.update();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		return cpane;
	}

	public void dispose() {
		if (cpane != null) {
			cpane.dispose();
			cpane = null;
		}
	}
}
