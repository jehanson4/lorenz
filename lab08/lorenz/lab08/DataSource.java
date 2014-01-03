package lorenz.lab08;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author jehanson
 */
public abstract class DataSource implements Steppable {

	private static final String clsName = DataSource.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ===================================
	// Variables
	// ===================================

	private static int defaultNameCounter = 0;

	private String name;
	private final List<DataSourceListener> dsListeners;

	// ===================================
	// Creation
	// ===================================

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

	public abstract DataBox getDataBoundsHint();

	public abstract DataPoint getCurrentPoint();

	@Override
	public abstract double getCurrentTime();

	protected abstract void doReset();

	protected abstract void doStep();

	@Override
	public void reset() {
		doReset();
		DataSourceEvent event =
				new DataSourceEvent(this, getCurrentPoint(), getCurrentTime());
		for (DataSourceListener dsl : dsListeners) {
			dsl.dataSourceReset(event);
		}
	}

	@Override
	public void step() {
		final String mtdName = "step";
		doStep();
		DataSourceEvent event =
				new DataSourceEvent(this, getCurrentPoint(), getCurrentTime());
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

	protected static String getDefaultName() {
		return DataSource.class.getSimpleName() + defaultNameCounter++;
	}
}
