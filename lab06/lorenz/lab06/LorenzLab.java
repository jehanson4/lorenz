package lorenz.lab06;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The main app.
 * 
 * @author jehanson4
 */
public class LorenzLab {

	// ==========================================
	// main
	// ==========================================

	public static void main(String[] args) {

		try {
			InputStream loggerStream = new FileInputStream(
					"lab06/logging.properties");
			LogManager.getLogManager().readConfiguration(loggerStream);
		} catch (Exception e) {
			System.out.println("Ignoring problem with logger configuration: "
					+ e);
		}

		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(LorenzLab.class.getSimpleName());
		shell.setSize(600, 300);
		shell.setLayout(new GridLayout(2, false));

		LorenzLab llab = new LorenzLab();
		Control vpane = llab.getViewer().buildControls(shell);
		vpane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Control cpane = llab.getController().buildControls(shell);
		cpane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		llab.getController().dispose();
		display.dispose();
	}

	// ==========================================
	// Variables
	// ==========================================

	private DataSourceContainer sources;
	private Viewer viewer;
	private Controller controller;

	// ==========================================
	// Creation
	// ==========================================

	public LorenzLab() {
		super();

		LorenzSystem sys = new LorenzSystem();
		DataPoint pt1 = sys.getInitialStateHint();
		DataSource ds1 = new RungeKutta4_3D("ds1", sys, pt1);
		DataPoint pt2 = new DataPoint(pt1.getX()+1E-5, pt1.getY(), pt1.getZ());
		DataSource ds2 = new RungeKutta4_3D("ds2", sys, pt2);
				
		this.sources = new DataSourceContainer();
		sources.add(ds1);
		sources.add(ds2);

		this.viewer = new Viewer();
		viewer.setDataBounds(sys.getDataBoundsHint());
		for (DataSource ds : sources.values()) {
			viewer.addTimeseries(ds);
		}

		this.controller = new Controller(sources);
	}

	// ==========================================
	// Operation
	// ==========================================

	public Viewer getViewer() {
		return viewer;
	}

	public Controller getController() {
		return controller;
	}

}
