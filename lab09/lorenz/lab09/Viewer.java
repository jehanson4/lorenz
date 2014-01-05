package lorenz.lab09;

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
import org.eclipse.swt.widgets.Display;

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
		public abstract String getName();

		public abstract Color getColor();

		public abstract void setColor(Color c);

		public abstract double getMaxHistory();

		public abstract void setMaxHistory(double maxHistory);
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

		public void teardown() {
			ds.removeDataSourceListener(this);
			eventsToDraw.clear();
		}

		@Override
		public String getName() {
			return ds.getName();
		}

		@Override
		public Color getColor() {
			return pointColor;
		}

		@Override
		public void setColor(Color c) {
			pointColor = c;
		}

		@Override
		public double getMaxHistory() {
			return this.maxHistory;
		}

		@Override
		public void setMaxHistory(double maxHistory) {
			this.maxHistory = maxHistory;
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
			// set overall most recent timestamp
			synchronized (eventsToDraw) {
				eventsToDraw.addLast(e);
				double oldestToKeep = e.getTimestamp() - maxHistory;
				while (!eventsToDraw.isEmpty()
						&& eventsToDraw.peekFirst().getTimestamp() < oldestToKeep)
					eventsToDraw.removeFirst();
			}
			redraw();
		}

		private void draw(GC gc, SimpleGraphicsTransform transform) {
			final String mtdName = "draw";

			// Color bg = gc.getBackground();
			Color fg = gc.getForeground();
			Point canvasPt;
			gc.setForeground(pointColor);
			// gc.setBackground(pointColor);
			synchronized (eventsToDraw) {
				if (logger.isLoggable(Level.FINE))
					logger.logp(Level.FINE, clsName, mtdName,
							"Drawing " + eventsToDraw.size() + " data points");
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
		public void controlMoved(ControlEvent e) {}

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

			// gc.setForeground(white);
			// gc.drawText("t=" + latestEventTimestamp, 0, 0);

		}
	}

	// ==================================
	// Variables
	// ==================================

	private static final int[] SYSTEM_COLORS = { SWT.COLOR_RED, SWT.COLOR_GREEN,
			SWT.COLOR_BLUE, SWT.COLOR_CYAN, SWT.COLOR_DARK_MAGENTA };

	private SimpleGraphicsTransform transform;
	private DataBox dataBounds;
	private Canvas canvas;
	private volatile Display display;
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
		this.display = null;
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
			throw new IllegalArgumentException("datasource with name \"" + name
					+ "\" is already present");
		TimeseriesImpl ts = new TimeseriesImpl(ds);
		assignDefaultColor(ts, timeseriesMap.size());
		timeseriesMap.put(name, ts);
		return ts;
	}

	public Timeseries getTimeseries(String name) {
		return timeseriesMap.get(name);
	}

	public void removeTimeseries(String name) {
		TimeseriesImpl ts = timeseriesMap.remove(name);
		if (ts != null)
			ts.teardown();
	}

	public Control buildControls(Composite parent) {
		display = parent.getDisplay();
		canvas = new Canvas(parent, SWT.NONE);

		canvas.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		// ensure colors of already-added timeseries are not null.
		int idx = 0;
		for (Timeseries ts : timeseriesMap.values()) {
			assignDefaultColor(ts, idx++);
		}

		CListener clistener = new CListener();
		canvas.addControlListener(clistener);
		canvas.addPaintListener(clistener);

		return canvas;
	}

	// ===================================
	// Private
	// ===================================

	private void redraw() {
		if (!redrawRequested) {
			redrawRequested = true;
			if (display != null && !display.isDisposed()) {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						redrawRequested = false;
						if (!canvas.isDisposed())
							canvas.redraw();
					}
				});
			}
		}
	};

	private void assignDefaultColor(Timeseries ts, int idx) {
		if (ts.getColor() == null && canvas != null && !canvas.isDisposed()) {
			ts.setColor(canvas.getDisplay().getSystemColor(
					SYSTEM_COLORS[idx % SYSTEM_COLORS.length]));
		}
	}

}
