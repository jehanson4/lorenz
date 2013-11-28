package lorenz.lab02;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
public class LorenzLab {

	// ==========================================
	// main
	// ==========================================

	public static void main(String[] args) {		

		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(LorenzLab.TITLE);
		shell.setSize(300, 300);
		
		LorenzLab app = new LorenzLab();
		app.buildComponents(shell);
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	// ===========================
	// Inner classes
	// ===========================

	private class CListener implements ControlListener, PaintListener {

		@Override
		public void controlMoved(ControlEvent e) {}

		@Override
		public void controlResized(ControlEvent e) {
			gf.initialize(dataBounds, canvas.getClientArea());
		}

		@Override
		public void paintControl(PaintEvent e) {
			// TODO: draw a nice cube.
			// GC gc = e.gc;
		}	
	}
	

	// ==========================================
	// Variables
	// ==========================================
	
	public static final String TITLE = "Lorenz Lab v0.2";
	
	private Canvas canvas;
	private GraphicsTransform gf;
	private DataBox dataBounds;
	
	// ==========================================
	// Creation
	// ==========================================

	public LorenzLab() {
		super();
		this.canvas = null;
		this.gf = new NormalTransform();
		this.dataBounds = DataBox.cubeAt(DataPoint.ORIGIN, 1.0);
	}

	// ==========================================
	// Operation
	// ==========================================

	public void buildComponents(Composite parent) {
		parent.setLayout(new FillLayout());
		this.canvas = new Canvas(parent, SWT.NONE);
		
		CListener clistener = new CListener();
		canvas.addControlListener(clistener);
		canvas.addPaintListener(clistener);
	}

}
