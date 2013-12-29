package lorenz.lab07;

/**
 * 
 * @author jehanson
 */
public interface RunnerControlListener {

	public void runnerStarted(RunnerControl source);
	public void runnerStopped(RunnerControl source);
	public void runnerReset(RunnerControl source);
}
