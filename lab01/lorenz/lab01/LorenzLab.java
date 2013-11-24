package lorenz.lab01;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
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

	// ==========================================
	// main
	// ==========================================

	public static void main(String[] args) {		

		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Lorenz Lab 1");
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

	// ==========================================
	// Variables
	// ==========================================
	
	Canvas canvas;
	
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

	public void buildComponents(Composite parent) {
		parent.setLayout(new FillLayout());
		canvas = new Canvas(parent, SWT.NONE);
		canvas.addPaintListener(this);
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		String msg = "Lorenz Lab 1";
		Rectangle rect = canvas.getClientArea();
		Point msgSize  = gc.textExtent(msg);
		int x = (rect.width - msgSize.x)/2;
		int y = (rect.height - msgSize.y)/2;
		gc.drawText("Lorenz Lab 01", x, y);
	}
	
}
