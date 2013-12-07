package lorenz.lab02;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
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
					"lab02/logging.properties");
			LogManager.getLogManager().readConfiguration(loggerStream);
		} catch (Exception e) {
			System.out.println("Ignoring problem with logger configuration: "
					+ e);
		}

		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(LorenzLab.class.getSimpleName());
		shell.setSize(300, 300);
		shell.setLayout(new FillLayout());

		LorenzLab llab = new LorenzLab();
		Viewer viewer = llab.getViewer();
		viewer.addSamplePoint(new DataPoint(0.0, 0.0, 0.0));
		viewer.addSamplePoint(new DataPoint(0.5, 0.0, 0.5));
		viewer.addSamplePoint(new DataPoint(0.5, 0.0, -0.5));
		viewer.addSamplePoint(new DataPoint(-0.5, 0.0, 0.5));
		viewer.addSamplePoint(new DataPoint(-0.5, 0.0, -0.5));

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

	public static final String TITLE = "Lorenz Lab v0.2";

	private Viewer viewer;

	// ==========================================
	// Creation
	// ==========================================

	public LorenzLab() {
		super();
		this.viewer = new Viewer();
	}

	// ==========================================
	// Operation
	// ==========================================

	public Viewer getViewer() {
		return viewer;
	}
	
}
