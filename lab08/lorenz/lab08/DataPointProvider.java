package lorenz.lab08;

import java.util.Properties;

/**
 * 
 * @author jehanson
 */
public class DataPointProvider implements ParameterProvider {

	private final DataPoint defaultPoint;
	private double x;
	private double y;
	private double z;
	
	public DataPointProvider(DataPoint defaultPoint) {
		this.defaultPoint = defaultPoint;
		x = defaultPoint.getX();
		y = defaultPoint.getY();
		z = defaultPoint.getZ();
	}
	
	public DataPoint getPoint() {
		return new DataPoint(x, y, z);
	}
	
	@Override
	public void setParameters(Properties params) {
		try {
			String s = params.getProperty("x");
			if (s != null)
				x = Double.parseDouble(s);
		}
		catch (Exception e) {
			// Ignore
		}

		try {
			String s = params.getProperty("y");
			if (s != null)
				y = Double.parseDouble(s);
		}
		catch (Exception e) {
			// Ignore
		}

		try {
			String s = params.getProperty("z");
			if (s != null)
				z = Double.parseDouble(s);
		}
		catch (Exception e) {
			// Ignore
		}
	}

	@Override
	public Properties getParameterDefaults() {
		Properties p = new Properties();
		p.setProperty("x", String.valueOf(defaultPoint.getX()));
		p.setProperty("y", String.valueOf(defaultPoint.getY()));
		p.setProperty("z", String.valueOf(defaultPoint.getZ()));
		return p;
	}

	@Override
	public Properties getParameters() {
		Properties p = new Properties();
		p.setProperty("x", String.valueOf(x));
		p.setProperty("y", String.valueOf(y));
		p.setProperty("z", String.valueOf(z));
		return p;
	}

}
