package lorenz.lab04;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DataSource {

	private static final String clsName = DataSource.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);
	
	// ===================================
	// Variables
	// ===================================

	private String name;
	private final List<DataSourceListener> dsListeners;

	// ===================================
	// Creation
	// ===================================

	public DataSource() {
		this("");
	}

	public DataSource(String name) {
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		this.name = name;
		this.dsListeners = new ArrayList<DataSourceListener>();
	}

	// ===================================
	// Operation
	// ===================================

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		this.name = name;
	}
	
	public abstract DataBox getDataBoundsHint();
	
	public abstract DataPoint getCurrentPoint();

	public abstract double getCurrentTime();

	protected abstract void doReset();

	protected abstract void doStep();

	public void reset() {
		doReset();
		DataSourceEvent event = new DataSourceEvent(this, getCurrentPoint(),
				getCurrentTime());
		for (DataSourceListener dsl : dsListeners) {
			dsl.dataSourceReset(event);
		}
	}

	public void step() {
		final String mtdName = "step";
		doStep();
		DataSourceEvent event = new DataSourceEvent(this, getCurrentPoint(),
				getCurrentTime());
		if (logger.isLoggable(Level.FINE)) {
			logger.logp(Level.FINE, clsName, mtdName, "event=" + event);
		}
		for (DataSourceListener dsl : dsListeners) {
			dsl.dataPointGenerated(event);
		}
	}

	public void addDataSourceListener(DataSourceListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null");
		dsListeners.add(listener);
	}

	public void removeDataSourceListener(DataSourceListener listener) {
		dsListeners.remove(listener);
	}
}
