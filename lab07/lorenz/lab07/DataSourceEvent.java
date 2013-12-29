package lorenz.lab07;

public class DataSourceEvent {

	private final Steppable source;
	private final DataPoint p;
	private final double t;

	public DataSourceEvent(Steppable source, DataPoint p, double t) {
		this.source = source;
		this.p = p;
		this.t = t;
	}

	public Steppable getSource() {
		return source;
	}

	public DataPoint getDataPoint() {
		return p;
	}

	public double getTimestamp() {
		return t;
	}
	
	@Override
	public String toString() {
		return "{p=" + p + ", t=" + t + "}";
	}
}
