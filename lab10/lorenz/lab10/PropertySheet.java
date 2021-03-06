package lorenz.lab10;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author jehanson
 */
public class PropertySheet {

	private static final String clsName = PropertySheet.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);
	
	// =====================================
	// Inner classes
	// =====================================

	public interface FieldValidator {
		// TODO: better exception class.
		public void validate(String s) throws InvalidFieldException;
	}

	public static class DefaultValidator implements PropertySheet.FieldValidator {

		public static final DefaultValidator instance = new DefaultValidator();

		@Override
		public void validate(String s) throws InvalidFieldException {
			// NOP
		}
	}

	public static class DoubleValidator implements PropertySheet.FieldValidator {

		private static DoubleValidator ANY = null;

		private final double lowerBound;
		private final boolean lowerBoundClosed;
		private final double upperBound;
		private final boolean upperBoundClosed;

		/**
		 * @param lowerBound
		 *            the lower bound, or Double.NEGATIVE_INFINITY
		 * @param lowerBoundClosed
		 *            if true, then the lower bound itself is considered valid.
		 * @param upperBound
		 * @param upperBoundClosed
		 */
		public DoubleValidator(double lowerBound, boolean lowerBoundClosed,
				double upperBound, boolean upperBoundClosed) {
			super();
			this.lowerBound = lowerBound;
			this.lowerBoundClosed = lowerBoundClosed;
			this.upperBound = upperBound;
			this.upperBoundClosed = upperBoundClosed;
		}

		/**
		 * Factory method: all x are valid. Multiple calls will return
		 * references to the same object. This method is not threadsafe.
		 * 
		 * @return DoubleValidator that accepts any string that can be parsed
		 *         into a double.
		 */
		public static DoubleValidator any() {
			if (ANY == null)
				ANY =
						new DoubleValidator(Double.NEGATIVE_INFINITY, false,
								Double.POSITIVE_INFINITY, false);
			return ANY;
		}

		/**
		 * Factory method: all {x : x < bound} are valid.
		 * 
		 * @param bound
		 * @return
		 */
		public static DoubleValidator lessThan(double bound) {
			return new DoubleValidator(Double.NEGATIVE_INFINITY, false, bound, false);
		}

		/**
		 * Factory method: all {x : x <= bound} are valid.
		 * 
		 * @param bound
		 * @return
		 */
		public static DoubleValidator lessOrEqual(double bound) {
			return new DoubleValidator(Double.NEGATIVE_INFINITY, false, bound, true);
		}

		/**
		 * Factory method: all {x : x > bound} are valid.
		 * 
		 * @param bound
		 * @return
		 */
		public static DoubleValidator greaterThan(double bound) {
			return new DoubleValidator(bound, false, Double.POSITIVE_INFINITY, false);
		}

		/**
		 * Factory method: all {x : x >= bound} are valid.
		 * 
		 * @param bound
		 * @return
		 */
		public static DoubleValidator greaterOrEqual(double bound) {
			return new DoubleValidator(bound, true, Double.POSITIVE_INFINITY, false);
		}

		@Override
		public void validate(String s) throws InvalidFieldException {
			if (s == null)
				throw new InvalidFieldException("Bad value: cannot be null");

			final double x;
			try {
				x = Double.parseDouble(s);
			}
			catch (NumberFormatException e) {
				throw new InvalidFieldException("Bad value \"" + s
						+ "\": must be parseable to double");
			}

			if (Double.isNaN(x))
				throw new InvalidFieldException("Bad value (" + x + "): must be a number");
			if (lowerBoundClosed && !(x >= lowerBound))
				throw new InvalidFieldException("Bad value (" + x + "): must be >= "
						+ lowerBound);
			if (!lowerBoundClosed && !(x > lowerBound))
				throw new InvalidFieldException("Bad value (" + x + "): must be > "
						+ lowerBound);
			if (upperBoundClosed && !(x <= upperBound))
				throw new InvalidFieldException("Bad value (" + x + "): must be <= "
						+ upperBound);
			if (!upperBoundClosed && !(x < upperBound))
				throw new InvalidFieldException("Bad value (" + x + "): must be < "
						+ upperBound);
		}
	}

	private class Row implements SelectionListener, FocusListener {

		private final String key;
		private String value;
		private final FieldValidator validator;
		private final boolean editable;
		private Text field;

		public Row(String key, String value, FieldValidator validator, boolean editable) {
			super();
			this.key = key;
			this.value = value;
			this.validator = (validator == null) ? DefaultValidator.instance : validator;
			this.editable = editable;
			this.field = null;
		}

		public void build() {
			Label label = new Label(control, SWT.RIGHT);
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			label.setText(key + keyValueSeparator);

			field = new Text(control, SWT.SINGLE);
			field.setText(String.valueOf(value));
			GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd.widthHint = fieldWidthHint;
			field.setLayoutData(gd);
			field.setEditable(editable);
			field.addSelectionListener(this);
			field.addFocusListener(this);
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(String v) throws InvalidFieldException {
			validator.validate(v); // No manual override.
			this.value = v;
			if (field != null && !field.isDisposed()) {
				field.setText(v);
			}
		}

		@Override
		public void focusGained(FocusEvent e) {}

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
			final String mtdName = "Row.apply";
			String v2 = field.getText();
			if (value.equals(v2)) {
				// NOP
			}
			else {
				try {
					validator.validate(v2);
					value = v2;
					firePropertyChange(key, value);
				}
				catch (Exception err) {
					if (logger.isLoggable(Level.FINE)) {
						logger.logp(Level.FINE, clsName, mtdName, err.getMessage() + " key=" + key);
					}
					// reset field to current value.
					field.setText(value);
				}
			}
		}
	}

	// =====================================
	// Variables
	// =====================================

	public static final int NUMERIC_FIELD_WIDTH_HINT = 80;

	private Composite control;
	private int fieldWidthHint;
	private String keyValueSeparator;
	private final Map<String, Row> rows;
	private final List<PropertyChangeListener> pcListeners;

	// =====================================
	// Creation
	// =====================================

	public PropertySheet() {
		super();
		control = null;
		fieldWidthHint = SWT.DEFAULT;
		keyValueSeparator = " = ";
		rows = new LinkedHashMap<String, Row>(); // preserves insertion order
		pcListeners = new ArrayList<PropertyChangeListener>();
	}

	// =====================================
	// Operation
	// =====================================

	public void addProperty(String key, String value) {
		addProperty(key, value, null, true);
	}

	public void addProperty(String key, String value, boolean editable) {
		addProperty(key, value, null, editable);
	}

	public void addProperty(String key, String value, FieldValidator validator) {
		addProperty(key, value, validator, true);
	}

	public int getFieldWidthHint() {
		return this.fieldWidthHint;
	}

	public void setFieldWidthHint(int fieldWidthHint) {
		this.fieldWidthHint = fieldWidthHint;
	}

	public String getProperty(String key) {
		Row row = rows.get(key);
		if (row == null)
			throw new IllegalArgumentException("Key not found: " + key);
		return row.getValue();
	}

	public void setProperty(String key, String value) {
		final String mtdName = "setProperty";
		Row row = rows.get(key);
		if (row == null)
			throw new IllegalArgumentException("Key not found: " + key);
		try {
			row.setValue(value);
		}
		catch (InvalidFieldException err) {
			if (logger.isLoggable(Level.FINE)) {
				logger.logp(Level.FINE, clsName, mtdName, err.getMessage() + " key=" + key);
			}
		}
	}

	public Control buildControls(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		for (Row r : rows.values()) {
			r.build();
		}
		return control;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Argument \"listener\" cannot be null");
		pcListeners.add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcListeners.remove(listener);
	}

	// ==============================
	// Private
	// ==============================

	private void addProperty(String key, String value, FieldValidator validator,
			boolean editable) {
		if (rows.containsKey(key))
			throw new IllegalArgumentException("Key already in use: " + key);

		Row r = new Row(key, value, validator, editable);
		rows.put(key, r);
		if (control != null && !control.isDisposed()) {
			r.build();
			control.layout(true, true);
		}
	}

	private void firePropertyChange(String key, String value) {
		for (PropertyChangeListener listener : pcListeners) {
			listener.propertyChanged(this, key, value);
		}
	}
}
