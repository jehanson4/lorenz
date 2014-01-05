package lorenz.lab09;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	// =====================================
	// Inner classes
	// =====================================

	public interface FieldValidator {
		// TODO: better exception class.
		public void validate(String s) throws IllegalArgumentException;
	}

	public static class DefaultValidator implements PropertySheet.FieldValidator {

		public static final DefaultValidator instance = new DefaultValidator();
		
		@Override
		public void validate(String s) throws IllegalArgumentException {
			// NOP
		}		
	}
	
	public static class DoubleValidator implements PropertySheet.FieldValidator {

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
		 * Factory method: all x are valid.
		 * 
		 * @return
		 */
		public static DoubleValidator any() {
			return new DoubleValidator(Double.NEGATIVE_INFINITY, false, Double.POSITIVE_INFINITY, false);
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
		public void validate(String s) {
			if (s == null)
				throw new IllegalArgumentException("Bad value: cannot be null");
			
			final double x;
			try {
				 x = Double.parseDouble(s);
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Bad value \"" + s + "\": must be parseable to double");
			}

			if (Double.isNaN(x))
				throw new IllegalArgumentException("Bad value (" + x + "): must be a number");
			if (lowerBoundClosed && !(x >= lowerBound))
				throw new IllegalArgumentException("Bad value (" + x + "): must be >= " + lowerBound);
			if (!lowerBoundClosed && !(x > lowerBound))
				throw new IllegalArgumentException("Bad value (" + x + "): must be > " + lowerBound);
			if (upperBoundClosed && !(x <= upperBound))
				throw new IllegalArgumentException("Bad value (" + x + "): must be <= " + upperBound);
			if (!upperBoundClosed && !(x < upperBound))
				throw new IllegalArgumentException("Bad value (" + x + "): must be < " + upperBound);
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
			label.setText(key + " = ");

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

		public void setValue(String v) {
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
				catch (Exception e) {
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
	private final Map<String, Row> rows;
	private final List<PropertyChangeListener> pcListeners;

	// =====================================
	// Creation
	// =====================================

	public PropertySheet() {
		super();
		control = null;
		fieldWidthHint = SWT.DEFAULT;
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
		return rows.get(key).getValue();
	}

	public void setProperty(String key, String value) {
		rows.get(key).setValue(value);
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
