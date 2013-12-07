package lorenz.lab04;

public class DataSourceEvent {

	private final DataSource source;
	private final DataPoint p;
	private final double t;

	public DataSourceEvent(DataSource source, DataPoint p, double t) {
		this.source = source;
		this.p = p;
		this.t = t;
	}

	public DataSource getSource() {
		return source;
	}

	public DataPoint getDataPoint() {
		return p;
	}

	public double getTimestamp() {
		return t;
	}
	
	public String toString() {
		return "{p=" + p + ", t=" + t + "}";
	}
}
