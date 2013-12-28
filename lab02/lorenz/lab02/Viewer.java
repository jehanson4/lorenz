package lorenz.lab02;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * GUI stuff for viewing what's going on.
 * 
 * @author jehanson
 * 
 */
public class Viewer {

	private static final String clsName = Viewer.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ==================================
	// Inner classes
	// ==================================

	private class CListener implements ControlListener, PaintListener {

		@Override
		public void controlMoved(ControlEvent e) {
		}

		@Override
		public void controlResized(ControlEvent e) {
			transform.initialize(dataBounds, canvas.getClientArea());
		}

		@Override
		public void paintControl(PaintEvent e) {
			final String mtdName = "paintControl";
			if (logger.isLoggable(Level.FINE))
				logger.logp(Level.FINE,  clsName,  mtdName,  "redrawing");
			
			final GC gc = e.gc;
			gc.setBackground(pointColor);
			for (DataPoint p : samplePoints)
				drawPoint(gc, p);
		}
	}

	// ==================================
	// Variables
	// ==================================

	private SimpleGraphicsTransform transform;
	private DataBox dataBounds;
	private Canvas canvas;
	private Color pointColor;
	private List<DataPoint> samplePoints;

	// ==================================
	// Creation
	// ==================================

	public Viewer() {
		this.transform = new SimpleGraphicsTransform();
		this.dataBounds = new DataBox(DataPoint.ORIGIN, 2.0);
		this.canvas = null;
		this.pointColor = null;
		this.samplePoints = new ArrayList<DataPoint>();
	}

	// ==================================
	// Operation
	// ==================================

	public void addSamplePoint(DataPoint p) {
		samplePoints.add(p);
	}

	public DataBox getDataBounds() {
		return dataBounds;
	}

	public void setDataBounds(DataBox bounds) {
		this.dataBounds = bounds;
		if (canvas != null && !canvas.isDisposed())
			transform.initialize(bounds, canvas.getClientArea());
	}

	public void buildControls(Composite parent) {
		canvas = new Canvas(parent, SWT.NONE);
		pointColor = parent.getDisplay().getSystemColor(SWT.COLOR_RED);

		CListener clistener = new CListener();
		canvas.addControlListener(clistener);
		canvas.addPaintListener(clistener);
	}

	// ============================================
	// Private
	// ============================================

	private void drawPoint(GC gc, DataPoint dataPt) {
		Point p = transform.dataToGraphics(dataPt);
		gc.drawText(dataPt.toString(), p.x + 4, p.y + 4, true);
		Color oldFG = gc.getForeground();
		gc.setForeground(pointColor);
		gc.drawLine(p.x, p.y - 8, p.x, p.y + 8);
		gc.drawLine(p.x - 8, p.y, p.x + 8, p.y);
		gc.setForeground(oldFG);
	}

}
