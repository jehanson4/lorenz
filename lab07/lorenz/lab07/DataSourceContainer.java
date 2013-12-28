package lorenz.lab07;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataSourceContainer extends AbstractMap<String, DataSource> implements Steppable {

	private final Map<String, DataSource> sources;
	private double currentT;
	private double deltaT;

	
	public DataSourceContainer() {
		super();
		sources = new HashMap<String, DataSource>();
		currentT = 0;
		deltaT = 0.01;
	}

	@Override
	public void step() {
		double stopTime = currentT + deltaT;
		for (DataSource s : sources.values()) {
			while (s.getCurrentTime() < stopTime)
				s.step();
		}
		currentT = stopTime;
	}

	@Override
	public void reset() {
		for (DataSource s : sources.values()) {
			s.reset();
		}
		currentT = 0.0;
	}

	@Override
	public double getCurrentTime() {
		return currentT;
	}

	public boolean isEmpty() {
		return sources.isEmpty();
	}

	public int size() {
		return sources.size();
	}

	@Override
	public Set<Map.Entry<String, DataSource>> entrySet() {
		return sources.entrySet();
	}

	public DataSource get(String name) {
		return sources.get(name);
	}

	public void add(DataSource source) {
		String name = source.getName();
		if (sources.containsKey(name))
			throw new IllegalArgumentException("datasource with name \"" + name + "\" already present");
		sources.put(name, source);
	}

	public DataSource remove(String name) {
		return sources.remove(name);
	}

	public void clear() {
		sources.clear();
	}

}
