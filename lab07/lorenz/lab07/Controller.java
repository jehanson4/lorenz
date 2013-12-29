package lorenz.lab07;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class Controller {

	// ======================================
	// Variables
	// ======================================

	private final RunnerControls runnerControls;
	private final ScenarioControls scenarioControls;
	
	// ======================================
	// Creation
	// ======================================

	public Controller(DataSourceContainer sources, Viewer viewer) {
		this.runnerControls = new RunnerControls(sources);
		this.scenarioControls = new ScenarioControls(sources, viewer);
	}

	// ======================================
	// Operation
	// ======================================

	public void addScenario(String name, Scenario scenario) {
		scenarioControls.addScenario(name, scenario);
	}
	
	public Control buildControls(Composite parent) {
		Composite cpane = new Composite(parent, SWT.NONE);
		cpane.setLayout(new GridLayout(1, false));

		Control runnerPane = runnerControls.buildControls(cpane);
		runnerPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,  1, 1));

		Control scenarioPane = scenarioControls.buildControls(cpane);
		scenarioPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,  1, 1));
		
		return cpane;
	}

	public void dispose() {
		runnerControls.dispose();
		scenarioControls.dispose();
	}

}
