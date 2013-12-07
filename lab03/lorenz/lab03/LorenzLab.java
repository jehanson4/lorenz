package lorenz.lab03;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The main app.
 * 
 * @author jehanson4
 */
public class LorenzLab {

	private static final String clsName = LorenzLab.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ==========================================
	// main
	// ==========================================

	public static void main(String[] args) {

		try {
			InputStream loggerStream = new FileInputStream(
					"lab03/logging.properties");
			LogManager.getLogManager().readConfiguration(loggerStream);
		} catch (Exception e) {
			System.out.println("Ignoring problem with logger configuration: "
					+ e);
		}

		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(LorenzLab.class.getSimpleName());
		shell.setSize(500, 300);
		shell.setLayout(new FillLayout());

		LorenzLab llab = new LorenzLab();
		llab.generateSomePoints(10000);
		
		Viewer viewer = llab.getViewer();
		viewer.buildControls(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	// ==========================================
	// Variables
	// ==========================================

	private DataSource dataSource;
	private Viewer viewer;
	
	// ==========================================
	// Creation
	// ==========================================

	public LorenzLab() {
		super();
		this.dataSource = new RandomWalker();
		this.viewer = new Viewer();
		dataSource.addDataSourceListener(viewer);
	}

	// ==========================================
	// Operation
	// ==========================================

	public Viewer getViewer() {
		return viewer;
	}
	
	public void generateSomePoints(int n) {
		dataSource.reset();
		for (int i=0; i<n; i++)
			dataSource.step();
	}

}
