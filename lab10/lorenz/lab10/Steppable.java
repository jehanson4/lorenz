package lorenz.lab10;

public interface Steppable {

	public abstract void step();

	public abstract void reset();

	public abstract double getCurrentTime();

	public abstract void setTimeStep(double t);

	public abstract double getTimeStep();

}
