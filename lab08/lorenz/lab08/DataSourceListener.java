package lorenz.lab08;

public interface DataSourceListener {

	public void dataSourceReset(DataSourceEvent e);
	
	public void dataPointGenerated(DataSourceEvent e);
}
