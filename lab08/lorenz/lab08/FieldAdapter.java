package lorenz.lab08;

import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

public class FieldAdapter implements SelectionListener, FocusListener {

	private static final String clsName = FieldAdapter.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);
	
	private final ParameterProvider provider;
	private final Object key;
	private final Text field;

	public FieldAdapter(ParameterProvider provider, Object key, Text field) {
		super();
		this.provider = provider;
		this.key = key;
		this.field = field;
	}

	public void connect() {
		field.addSelectionListener(this);
		field.addFocusListener(this);
	}

	public void disconnecct() {
		field.removeSelectionListener(this);
		field.removeFocusListener(this);
	}

	public void updateField(Object key, Text field) {
		logger.info("updating field: key=" + key + " value="
				+ provider.getParameters().get(key));
		field.setText(String.valueOf(provider.getParameters().get(key)));
	}

	public void applyField(Object key, Text field) {
		logger.info("applying field: key=" + key + " value="
				+ field.getText());
		Properties pp = new Properties();
		pp.setProperty(String.valueOf(key), field.getText());
		provider.setParameters(pp);
		updateField(key, field);
		
	}

	@Override
	public final void focusGained(FocusEvent e) {
		updateField(key, field);
	}

	@Override
	public final void focusLost(FocusEvent e) {
		applyField(key, field);
	}

	@Override
	public final void widgetSelected(SelectionEvent e) {}

	@Override
	public final void widgetDefaultSelected(SelectionEvent e) {
		applyField(key, field);
	}

}