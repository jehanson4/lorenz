package lorenz.lab07;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RunnerControl {

	private static final String clsName = RunnerControl.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	// ======================================
	// Inner classes
	// ======================================

	private class RunLoop implements Runnable {

		@Override
		public void run() {
			final String mtdName = "RunLoop.run";
			if (logger.isLoggable(Level.FINE))
				logger.logp(Level.FINE, clsName, mtdName, "entering");

			execOnUIThread(new Runnable() {
				@Override
				public void run() {
					for (RunnerControlListener listener : rcListeners) {
						listener.runnerStarted(RunnerControl.this);
					}
				}
			});

			long t0, sleepTime;
			while (runnerThread == Thread.currentThread()) {
				t0 = System.currentTimeMillis();
				steppable.step();
				sleepTime = delayMillis - (System.currentTimeMillis() - t0);
				if (sleepTime > 0) {
					try {
						Thread.sleep(sleepTime);
					}
					catch (InterruptedException e) {
						// reset interrupted flag and exit run-loop
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
			// if (logger.isLoggable(Level.FINE))
			// logger.logp(Level.FINE, clsName, mtdName, "broke out of loop");

			execOnUIThread(new Runnable() {
				@Override
				public void run() {
					for (RunnerControlListener listener : rcListeners) {
						listener.runnerStopped(RunnerControl.this);
					}
				}
			});

			if (logger.isLoggable(Level.FINE))
				logger.logp(Level.FINE, clsName, mtdName, "exiting");
		}

	}

	// ======================================
	// Variables
	// ======================================

	private final Steppable steppable;
	private long delayMillis;
	private final Object runnerGuard = new Object();
	private volatile Thread runnerThread;
	private Image startImage;
	private Image stopImage;
	private Image stepImage;
	private Image resetImage;
	private final List<RunnerControlListener> rcListeners;
	private Composite cpane;
	private volatile Display display;

	// ======================================
	// Creation
	// ======================================

	public RunnerControl(Steppable steppable) {
		this.steppable = steppable;
		this.delayMillis = 1;
		this.rcListeners = new CopyOnWriteArrayList<RunnerControlListener>();
	}

	// ======================================
	// Operation
	// ======================================

	public long getDelayMillis() {
		return this.delayMillis;
	}

	public void setDelayMillis(long millis) {
		if (!(millis >= 0))
			throw new IllegalArgumentException("millis must be > 0");
		this.delayMillis = millis;
	}

	public Control buildControls(Composite parent) {
		display = parent.getDisplay();

		cpane = new Composite(parent, SWT.NONE);
		cpane.setLayout(new GridLayout(4, true));

		Display display = parent.getDisplay();

		resetImage = loadImage(display, "resources/reset.gif");
		startImage = loadImage(display, "resources/start.gif");
		stepImage = loadImage(display, "resources/step.gif");
		stopImage = loadImage(display, "resources/stop.gif");

		Button resetButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		resetButton.setImage(resetImage);
		resetButton.setToolTipText("reset");
		resetButton
				.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				reset();
			}
		});

		Button startButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		startButton.setImage(startImage);
		startButton.setToolTipText("start");
		startButton
				.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				start();
			}
		});

		Button stepButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		stepButton.setImage(stepImage);
		stepButton.setToolTipText("step");
		stepButton
				.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		stepButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				step();
			}
		});

		Button stopButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		stopButton.setImage(stopImage);
		stopButton.setToolTipText("stop");
		stopButton
				.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stop();
			}
		});

		Label delayMillisLabel = new Label(cpane, SWT.RIGHT);
		delayMillisLabel.setText("delay per step (millisec):");
		delayMillisLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		final Text delayMillisText = new Text(cpane, SWT.SINGLE | SWT.RIGHT);
		delayMillisText.setText(String.valueOf(getDelayMillis()));
		delayMillisText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		delayMillisText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {
					setDelayMillis(Integer.parseInt(delayMillisText.getText()));
				}
				catch (Exception err) {
					delayMillisText.setText(String.valueOf(getDelayMillis()));
				}
			}
		});

		return cpane;
	}

	public void dispose() {
		if (startImage != null)
			startImage.dispose();
		if (stopImage != null)
			stopImage.dispose();
		if (stepImage != null)
			stepImage.dispose();
		if (resetImage != null)
			resetImage.dispose();
	}

	public void start() {
		final String mtdName = "start";
		if (logger.isLoggable(Level.FINE))
			logger.logp(Level.FINE, clsName, mtdName, "starting");
		synchronized (runnerGuard) {
			if (runnerThread == null) {
				runnerThread = new Thread(new RunLoop());
				runnerThread.start();
			}
		}
	}

	public void stop() {
		final String mtdName = "stop";
		if (logger.isLoggable(Level.FINE))
			logger.logp(Level.FINE, clsName, mtdName, "stopping");
		synchronized (runnerGuard) {
			runnerThread = null;
		}
	}

	public void step() {
		synchronized (runnerGuard) {
			if (runnerThread == null)
				steppable.step();
		}
	}

	public void reset() {
		boolean wasReset = false;
		synchronized (runnerGuard) {
			if (runnerThread == null) {
				steppable.reset();
				wasReset = true;
			}
		}
		if (wasReset) {
			for (RunnerControlListener listener : rcListeners)
				listener.runnerReset(this);
		}
	}

	public void addRunnerControlListener(RunnerControlListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Argument \"listener\" cannot be null");
		rcListeners.add(listener);
	}

	public void removeRunnerControlListener(RunnerControlListener listener) {
		rcListeners.remove(listener);
	}

	// =================================
	// Private
	// =================================

	private void execOnUIThread(Runnable r) {
		if (display == null || display.isDisposed()
				|| display.getThread() == Thread.currentThread())
			r.run();
		else
			display.asyncExec(r);
	}

	private Image loadImage(Display display, String fileName) {
		final String mtdName = "loadImage";
		Image image = null;
		try {
			URL url = RunnerControl.class.getResource(fileName);
			if (logger.isLoggable(Level.FINE))
				logger.logp(Level.FINE, clsName, mtdName, "image url: " + url);
			InputStream is = url.openStream();
			image = new Image(display, is);
			is.close();
		}
		catch (IOException e) {
			// Ignore
		}
		return image;
	}

}
