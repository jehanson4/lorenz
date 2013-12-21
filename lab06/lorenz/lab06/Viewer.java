package lorenz.lab06;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
public class Viewer {

	private static final String clsName = Viewer.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ==================================
	// Inner classes
	// ==================================

	public interface Timeseries {
		public String getName();
		public Color getColor();
		public void setColor(Color c);
	}
	
	private final class TimeseriesImpl implements Timeseries, DataSourceListener {

		private final DataSource ds;
		private Color pointColor;
		private final LinkedList<DataSourceEvent> eventsToDraw;
		private double maxHistory;

		public TimeseriesImpl(DataSource ds) {
			this.ds = ds;
			this.pointColor = null;
			this.eventsToDraw = new LinkedList<DataSourceEvent>();
			this.maxHistory = 4.0;
			
			ds.addDataSourceListener(this);
		}

		public String getName() {
			return ds.getName();
		}
		
		public Color getColor() {
			return pointColor;
		}
		
		public void setColor(Color c) {
			pointColor = c;
		}
		
		@Override
		public void dataSourceReset(DataSourceEvent e) {
			synchronized (eventsToDraw) {
				eventsToDraw.clear();
			}
			redraw();
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
			redraw();
		}

		void draw(GC gc, SimpleGraphicsTransform transform) {
			final String mtdName = "draw";

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
					// MAYBE: if (event.getTimestamp() <= maxTime) { ...
					canvasPt = transform.dataToGraphics(event.getDataPoint());
					if (logger.isLoggable(Level.FINER)) {
						logger.logp(Level.FINE, clsName, mtdName, "drawing: event="
								+ event + " canvasPt=" + canvasPt);
					}
					gc.drawPoint(canvasPt.x, canvasPt.y);
					// gc.fillRectangle(canvasPt.x - 1, canvasPt.y - 1, 3, 3);
				}
			}
			gc.setForeground(fg);
			// gc.setBackground(bg);
		}
	}

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

			for (TimeseriesImpl t : timeseriesMap.values()) {
				t.draw(gc, transform);
			}
			// gc.drawText("t=" + latestEventTimestamp, 0, 0);

		}
	}

	// ==================================
	// Variables
	// ==================================

	private static final int[] SYSTEM_COLORS = {
		SWT.COLOR_RED,
		SWT.COLOR_GREEN,
		SWT.COLOR_BLUE,
		SWT.COLOR_CYAN,
		SWT.COLOR_DARK_MAGENTA
	};

	private SimpleGraphicsTransform transform;
	private DataBox dataBounds;
	private Canvas canvas;
	private volatile boolean clearRequested;
	private volatile boolean redrawRequested;
	private final Map<String, TimeseriesImpl> timeseriesMap;

	// ==================================
	// Creation
	// ==================================

	public Viewer() {
		this.transform = new SimpleGraphicsTransform();
		this.dataBounds = new DataBox(DataPoint.ORIGIN, 2.0);
		this.canvas = null;
		this.clearRequested = false;
		this.redrawRequested = false;
		this.timeseriesMap = new HashMap<String, TimeseriesImpl>();
		// this.maxHistory = 10000.0;
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

	public Timeseries addTimeseries(DataSource ds) {
		String name = ds.getName();
		if (timeseriesMap.containsKey(name)) 
			throw new IllegalArgumentException("datasource with name \"" + name + "\" is already present");
		TimeseriesImpl ts = new TimeseriesImpl(ds);
		timeseriesMap.put(name, ts);
		return ts;
	}
	
	public Control buildControls(Composite parent) {
		canvas = new Canvas(parent, SWT.BORDER | SWT.NO_BACKGROUND);
		CListener clistener = new CListener();
		canvas.addControlListener(clistener);
		canvas.addPaintListener(clistener);

		// ensure timeseries colors are not null.
		int idx = 0;
		for (TimeseriesImpl ts : timeseriesMap.values()) {
			if (ts.getColor() == null) {
				ts.setColor(canvas.getDisplay().getSystemColor(SYSTEM_COLORS[idx % SYSTEM_COLORS.length]));
				idx++;
			}
		}

		return canvas;
	}

	/**
	 * Package private: for use by the Timeseries objects
	 */
	void redraw() {
		if (!redrawRequested && canvas != null && !canvas.isDisposed()) {
			canvas.getDisplay().asyncExec(new Runnable() {
				public void run() {
					redrawRequested = false;
					if (!canvas.isDisposed())
						canvas.redraw();
				}
			});
		}
	};
}
