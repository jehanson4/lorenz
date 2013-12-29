package lorenz.lab07;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
public class ScenarioControls {

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
	private String installedScenarioName;
	private Scenario installedScenario;
	private Composite scenarioParent;

	// ==========================================
	// Creation
	// ==========================================

	/**
	 * @param sources
	 * @param viewer
	 */
	public ScenarioControls(DataSourceContainer sources, Viewer viewer) {
		this.sources = sources;
		this.viewer = viewer;
		this.scenarios = new HashMap<String, Scenario>();
		this.installedScenario = null;
		this.installedScenarioName = null;
		this.scenarioParent = null;
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
			throw new IllegalArgumentException("scenario name \"" + name + "\" already in use");
		
		// Need it to work when called BEFORE controls created, and AFTER.
		scenarios.put(name, scenario);
		initScenarioList();
	}
	
	public Control buildControls(Composite parent) {
		
		Composite cpane = new Composite(parent, SWT.NONE);
		cpane.setLayout(new GridLayout(2, false));
		
		Label scenarioLabel = new Label(cpane, SWT.RIGHT);
		scenarioLabel.setText("Scenario:");
		scenarioLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,1));
		
		scenarioCombo = new Combo(cpane, SWT.DROP_DOWN | SWT.READ_ONLY);
		scenarioCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,1));
		
		scenarioParent = new Composite(cpane, SWT.BORDER);
		scenarioParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,1));
		scenarioParent.setLayout(new FillLayout());

		initScenarioList();
		scenarioCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				installScenario(scenarioCombo.getText());
			}

		});

		return cpane;
	}

	public void dispose() {		
	}
	
	public void addSelectionListener(SelectionListener listener) {
		if (scenarioCombo == null)
			throw new IllegalStateException("Wait until after controls are created");
		scenarioCombo.addSelectionListener(listener);
	}
	// ==============================
	// Private
	// ==============================

	private void initScenarioList() {
		if (scenarioCombo != null && !scenarioCombo.isDisposed()) {
			scenarioCombo.removeAll();
			String[] scenarioNames = scenarios.keySet().toArray(new String[scenarios.size()]);
			Arrays.sort(scenarioNames);
			for (String name : scenarioNames) {
				scenarioCombo.add(name);
			}
		}
	}

	private void installScenario(String name) {
		Scenario sc = scenarios.get(name);
		if (sc != null) {
			if (installedScenario != null) {
				installedScenario.teardown(sources, viewer);
				if (scenarioParent != null)
					for (Control c : scenarioParent.getChildren())
						c.dispose();
			}
			this.installedScenarioName = name;
			this.installedScenario = sc;
			sc.setup(sources,  viewer);
			if (scenarioParent != null && !scenarioParent.isDisposed())
				sc.buildControls(scenarioParent);
			
		}
	}
	
	

}
