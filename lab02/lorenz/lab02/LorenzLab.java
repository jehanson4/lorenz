package lorenz.lab02;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
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


		try {
			InputStream loggerStream = new FileInputStream(
					"lab02/logging.properties");
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
			GC gc = e.gc;
			gc.setBackground(pointColor);
			drawPoint(gc, new DataPoint(0.0, 0.0, 0.0));
			drawPoint(gc, new DataPoint(0.4, 0.0, 0.4));
			drawPoint(gc, new DataPoint(0.4, 0.0, -0.4));
			drawPoint(gc, new DataPoint(-0.4, 0.0, 0.4));
			drawPoint(gc, new DataPoint(-0.4, 0.0, -0.4));
		}	
	}
	

	// ==========================================
	// Variables
	// ==========================================
	
	public static final String TITLE = "Lorenz Lab v0.2";
	
	private Canvas canvas;
	private Color  pointColor;
	private SimpleGraphicsTransform gf;
	private DataBox dataBounds;
	
	// ==========================================
	// Creation
	// ==========================================

	public LorenzLab() {
		super();
		this.canvas = null;
		this.pointColor = null;
		this.gf = new SimpleGraphicsTransform();
		this.dataBounds = new DataBox(DataPoint.ORIGIN, 1.0);
	}

	// ==========================================
	// Operation
	// ==========================================

	public void buildComponents(Composite parent) {
		parent.setLayout(new FillLayout());
		this.canvas = new Canvas(parent, SWT.NONE);
		this.pointColor = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
		
		CListener clistener = new CListener();
		canvas.addControlListener(clistener);
		canvas.addPaintListener(clistener);
	}

	// ============================================
	// Private
	// ============================================
	
	private void drawPoint(GC gc, DataPoint dataPt) {
		Point p = gf.dataToGraphics(dataPt);
		gc.drawText(dataPt.toString(), p.x, p.y, true);
		gc.fillRectangle(p.x-1, p.y-1, 3, 3);
	}
}
