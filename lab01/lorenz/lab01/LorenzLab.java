package lorenz.lab01;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The main app.
 * 
 * @author jehanson4
 */
public class LorenzLab implements PaintListener {

	private static String clsName = LorenzLab.class.getName();
	private static Logger logger = Logger.getLogger(clsName);

	// ==========================================
	// main
	// ==========================================

	public static void main(String[] args) {

		try {
			InputStream loggerStream = new FileInputStream(
					"lab01/logging.properties");
			LogManager.getLogManager().readConfiguration(loggerStream);
		} catch (Exception e) {
			System.out
					.println("Ignoring problem with logger configuration: "
							+ e);
		}

		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(LorenzLab.class.getSimpleName());
		shell.setSize(300, 300);
		shell.setLayout(new FillLayout());
		
		LorenzLab llab = new LorenzLab();
		llab.buildControls(shell);
		
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

	public static final String TITLE = "Lorenz Lab v0.1";

	private Canvas canvas;

	// ==========================================
	// Creation
	// ==========================================

	public LorenzLab() {
		super();
		canvas = null;
	}

	// ==========================================
	// Operation
	// ==========================================

	public void buildControls(Composite parent) {
		canvas = new Canvas(parent, SWT.NONE);
		canvas.addPaintListener(this);
	}

	@Override
	public void paintControl(PaintEvent e) {
		final String mtdName = "paintControl";
		GC gc = e.gc;
		Rectangle rect = canvas.getClientArea();
		int x = rect.x + rect.width / 2;
		int y = rect.y + rect.height  / 2;
		String msg = "(" + x + ", " + y + ")";
		if (logger.isLoggable(Level.FINE))
			logger.logp(Level.FINE,  clsName,  mtdName,  "redrawing");
		gc.drawText(msg, x+4, y+4);
		gc.drawLine(x, y-8, x, y+8);
		gc.drawLine(x-8, y, x+8, y);
	}

}
