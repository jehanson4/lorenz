package lorenz.lab03;

import java.util.LinkedList;
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
import org.eclipse.swt.widgets.Control;

/**
 * GUI stuff for viewing what's going on.
 * 
 * @author jehanson
 * 
 */
public class Viewer implements DataSourceListener {

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

			final GC gc = e.gc;

			if (clearRequested) {
				if (logger.isLoggable(Level.FINE))
					logger.logp(Level.FINE, clsName, mtdName, "clearing canvas");
				gc.fillRectangle(canvas.getClientArea());
				clearRequested = false;
			}

			// Color bg = gc.getBackground();
			Color fg = gc.getForeground();
			Point canvasPt;
			gc.setForeground(pointColor);
			// gc.setBackground(pointColor);
			synchronized (eventsToDraw) {
				if (logger.isLoggable(Level.FINE))
					logger.logp(Level.FINE, clsName, mtdName, "Drawing "
							+ eventsToDraw.size() + " data points");
				for (DataSourceEvent event : eventsToDraw) {
					canvasPt = transform.dataToGraphics(event.getDataPoint());
					if (logger.isLoggable(Level.FINER)) {
						logger.logp(Level.FINE, clsName, mtdName,
								"drawing: event=" + event + " canvasPt="
										+ canvasPt);
					}
					gc.drawPoint(canvasPt.x, canvasPt.y);
					// gc.fillRectangle(canvasPt.x - 1, canvasPt.y - 1, 3, 3);
				}
			}
			gc.setForeground(fg);
			// gc.setBackground(bg);
		}
	}

	// ==================================
	// Variables
	// ==================================

	private SimpleGraphicsTransform transform;
	private DataBox dataBounds;
	private Canvas canvas;
	private Color pointColor;
	private volatile boolean clearRequested;
	private final LinkedList<DataSourceEvent> eventsToDraw;
	private double maxHistory;

	// ==================================
	// Creation
	// ==================================

	public Viewer() {
		this.transform = new SimpleGraphicsTransform();
		this.dataBounds = new DataBox(DataPoint.ORIGIN, 2.0);
		this.canvas = null;
		this.pointColor = null;
		this.clearRequested = false;
		this.eventsToDraw = new LinkedList<DataSourceEvent>();
		this.maxHistory = 10000.0;
	}

	// ==================================
	// Operation
	// ==================================

	public DataBox getDataBounds() {
		return dataBounds;
	}

	public void setDataBounds(DataBox bounds) {
		this.dataBounds = bounds;
		clearRequested = true;
		if (canvas != null && !canvas.isDisposed())
			transform.initialize(bounds, canvas.getClientArea());
	}

	public Control buildControls(Composite parent) {
		canvas = new Canvas(parent, SWT.NONE);
		pointColor = parent.getDisplay().getSystemColor(SWT.COLOR_RED);

		CListener clistener = new CListener();
		canvas.addControlListener(clistener);
		canvas.addPaintListener(clistener);

		return canvas;
	}

	@Override
	public void dataSourceReset(DataSourceEvent e) {
		synchronized (eventsToDraw) {
			eventsToDraw.clear();
		}
		clearRequested = true;

		// Do it this way because canvas.redraw() may only be called on the
		// UI thread.
		if (canvas != null && !canvas.isDisposed()) {
			canvas.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!canvas.isDisposed())
						canvas.redraw();
				}
			});
		}
	}

	@Override
	public void dataPointGenerated(DataSourceEvent e) {
		synchronized (eventsToDraw) {
			eventsToDraw.addLast(e);
			double oldestToKeep = e.getTimestamp() - maxHistory;
			while (!eventsToDraw.isEmpty()
					&& eventsToDraw.peekFirst().getTimestamp() < oldestToKeep)
				eventsToDraw.removeFirst();
		}

		// Do it this way because canvas.redraw() may only be called on the
		// UI thread.
		if (canvas != null && !canvas.isDisposed()) {
			canvas.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!canvas.isDisposed())
						canvas.redraw();
				}
			});
		}
	}

}
