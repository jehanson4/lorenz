package lorenz.lab09;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author jehanson
 */
public class ScenarioControl implements RunnerControlListener {

	private static final String clsName = ScenarioControl.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ==========================================
	// Inner classes
	// ==========================================

	// ==========================================
	// Variables
	// ==========================================

	private final DataSourceContainer sources;
	private final Viewer viewer;
	private Combo scenarioCombo;
	private final Map<String, Scenario> scenarios;
	private String defaultScenarioName;
	private Scenario installedScenario;
	private Composite scenarioConfigParent;

	// ==========================================
	// Creation
	// ==========================================

	/**
	 * @param sources
	 * @param viewer
	 */
	public ScenarioControl(DataSourceContainer sources, Viewer viewer) {
		this.sources = sources;
		this.viewer = viewer;
		this.scenarios = new HashMap<String, Scenario>();
		this.installedScenario = null;
		this.defaultScenarioName = null;
		this.scenarioConfigParent = null;
	}

	// ==========================================
	// Operation
	// ==========================================

	public Set<String> getScenarioNames() {
		return Collections.unmodifiableSet(scenarios.keySet());
	}

	public void addScenario(String name, Scenario scenario) {
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		if (scenario == null)
			throw new IllegalArgumentException("scenario cannot be null");
		if (scenarios.containsKey(name))
			throw new IllegalArgumentException("scenario name \"" + name
					+ "\" already in use");

		// (Need this to work when called BEFORE controls created, and AFTER.)
		if (defaultScenarioName == null)
			defaultScenarioName = name;
		scenarios.put(name, scenario);
		initScenarioComboItems();
	}

	public Control buildControls(Composite parent) {

		Composite cpane = new Composite(parent, SWT.NONE);
		cpane.setLayout(new GridLayout(2, false));

		Label scenarioLabel = new Label(cpane, SWT.RIGHT);
		scenarioLabel.setText("Scenario:");
		scenarioLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		scenarioCombo = new Combo(cpane, SWT.DROP_DOWN | SWT.READ_ONLY);
		scenarioCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		scenarioConfigParent = new Composite(cpane, SWT.BORDER);
		scenarioConfigParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		scenarioConfigParent.setLayout(new FillLayout());

		// Configure the scenario combo.
		// It's important to add the selection listener after installing the
		// default scenario
		initScenarioComboItems();
		if (this.defaultScenarioName != null) {
			installScenario(defaultScenarioName);
			scenarioCombo.select(scenarioCombo.indexOf(defaultScenarioName));
		}
		scenarioCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				installScenario(scenarioCombo.getText());
			}
		});

		return cpane;
	}

	public void dispose() {}

	public void addSelectionListener(SelectionListener listener) {
		if (scenarioCombo == null)
			throw new IllegalStateException("Wait until after controls are created");
		scenarioCombo.addSelectionListener(listener);
	}

	@Override
	public void runnerStarted(RunnerControl source) {
		final String mtdName = "runnerStarted";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "disabling scenario controls");
		}
		scenarioCombo.setEnabled(false);
		scenarioConfigParent.setEnabled(false);
	}

	@Override
	public void runnerStopped(RunnerControl source) {
		final String mtdName = "runnerStopped";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "enabling scenario controls");
		}
		scenarioCombo.setEnabled(true);
		scenarioConfigParent.setEnabled(true);
	}

	@Override
	public void runnerReset(RunnerControl source) {
		final String mtdName = "runnerReset";
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "enter");
		}
	}

	// ==============================
	// Private
	// ==============================

	private void initScenarioComboItems() {

		// TODO: preserve info about currently installed scenario.

		if (scenarioCombo != null && !scenarioCombo.isDisposed()) {
			scenarioCombo.removeAll();
			String[] scenarioNames =
					scenarios.keySet().toArray(new String[scenarios.size()]);
			Arrays.sort(scenarioNames);
			for (String name : scenarioNames) {
				scenarioCombo.add(name);
			}
		}
	}

	private void installScenario(String name) {
		final String mtdName = "installScenario";
		Scenario sc = scenarios.get(name);
		if (sc == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.logp(Level.FINE, clsName, mtdName, "scenario " + name
						+ " not found.");
			}
		}
		else if (sc == installedScenario){
			if (logger.isLoggable(Level.FINE)) {
				logger.logp(Level.FINE, clsName, mtdName, "scenario " + name
						+ " already installed.");
			}
		}
		else {
			if (installedScenario != null) {
				installedScenario.teardown(sources, viewer);
				if (scenarioConfigParent != null)
					for (Control c : scenarioConfigParent.getChildren())
						c.dispose();
			}
			
			sources.reset();
			
			if (logger.isLoggable(Level.FINE)) {
				logger.logp(Level.FINE, clsName, mtdName, "installing scenario " + name);
			}
			this.installedScenario = sc;
			sc.setup(sources, viewer);
			if (scenarioConfigParent != null && !scenarioConfigParent.isDisposed()) {
				sc.buildControls(scenarioConfigParent);
				scenarioConfigParent.layout(true, true);
			}

		}
	}
}
