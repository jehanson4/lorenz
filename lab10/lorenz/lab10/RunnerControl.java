package lorenz.lab10;

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

	private abstract class AbstractRunLoop implements Runnable {

		protected abstract void innerLoop();

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

			innerLoop();

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

	private class RunLoopNoSleep extends AbstractRunLoop {

		@Override
		protected void innerLoop() {
			while (runnerThread == Thread.currentThread()) {
				steppable.step();
			}
		}
	}

//	private class RunLoopSleepPerStep extends AbstractRunLoop {
//
//		@Override
//		protected void innerLoop() {
//			long t0, sleepTime;
//			while (runnerThread == Thread.currentThread()) {
//				t0 = System.currentTimeMillis();
//				steppable.step();
//				sleepTime = delayNanos - (System.currentTimeMillis() - t0);
//				if (sleepTime > 0) {
//					try {
//						Thread.sleep(sleepTime);
//					}
//					catch (InterruptedException e) {
//						// reset interrupted flag and exit run-loop
//						Thread.currentThread().interrupt();
//						break;
//					}
//				}
//			}
//		}
//	}

	private class RunLoopBudgetNanos extends AbstractRunLoop {

		@Override
		protected void innerLoop() {
			final int delay = delayNanos;
			long sleepMillis = 0;
			int budgetNanos = 0;
			long t0 = System.currentTimeMillis();
			while (runnerThread == Thread.currentThread()) {
				budgetNanos += delay;
				steppable.step();
				sleepMillis = (long)(budgetNanos/1000 - (System.currentTimeMillis() - t0));
				if (sleepMillis > 0) {
					budgetNanos -= 1000*sleepMillis;
					try {
						Thread.sleep(sleepMillis);
					}
					catch (InterruptedException e) {
						// reset interrupted flag and exit run-loop
						Thread.currentThread().interrupt();
						break;
					}
					t0 = System.currentTimeMillis();
				}
			}
		}
	}


//	private class RunLoop implements Runnable {
//
//		@Override
//		public void run() {
//			final String mtdName = "RunLoop.run";
//			if (logger.isLoggable(Level.FINE))
//				logger.logp(Level.FINE, clsName, mtdName, "entering");
//
//			execOnUIThread(new Runnable() {
//				@Override
//				public void run() {
//					for (RunnerControlListener listener : rcListeners) {
//						listener.runnerStarted(RunnerControl.this);
//					}
//				}
//			});
//
//			long t0, sleepTime;
//			while (runnerThread == Thread.currentThread()) {
//				t0 = System.currentTimeMillis();
//				steppable.step();
//				sleepTime = delayMillis - (System.currentTimeMillis() - t0);
//				if (sleepTime > 0) {
//					try {
//						Thread.sleep(sleepTime);
//					}
//					catch (InterruptedException e) {
//						// reset interrupted flag and exit run-loop
//						Thread.currentThread().interrupt();
//						break;
//					}
//				}
//			}
//			// if (logger.isLoggable(Level.FINE))
//			// logger.logp(Level.FINE, clsName, mtdName, "broke out of loop");
//
//			execOnUIThread(new Runnable() {
//				@Override
//				public void run() {
//					for (RunnerControlListener listener : rcListeners) {
//						listener.runnerStopped(RunnerControl.this);
//					}
//				}
//			});
//
//			if (logger.isLoggable(Level.FINE))
//				logger.logp(Level.FINE, clsName, mtdName, "exiting");
//		}
//	}

	// ======================================
	// Variables
	// ======================================

	private final Object runnerGuard = new Object();
	private final Steppable steppable;
	private volatile Thread runnerThread;
	private volatile int delayNanos;
	private Image startImage;
	private Image stopImage;
	private Image stepImage;
	private Image resetImage;
	private final List<RunnerControlListener> rcListeners;
	private Composite cpane;
	private Button startButton;
	private Button stopButton;
	private Button resetButton;
	private Button stepButton;
	private Text delayField;
	private volatile Display display;

	// ======================================
	// Creation
	// ======================================

	public RunnerControl(Steppable steppable) {
		this.steppable = steppable;
		this.delayNanos = 1000;
		this.rcListeners = new CopyOnWriteArrayList<RunnerControlListener>();
	}

	// ======================================
	// Operation
	// ======================================

	public long getDelayNanos() {
		return this.delayNanos;
	}

	public void setDelayNanos(int nanos) {
		if (!(nanos >= 0))
			throw new IllegalArgumentException("nanos must be >= 0");
		this.delayNanos = nanos;
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

		resetButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
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

		startButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
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

		stepButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
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

		stopButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
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

		Label delayLabel = new Label(cpane, SWT.RIGHT);
		delayLabel.setText("Delay per step (nanosec):");
		delayLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		delayField = new Text(cpane, SWT.SINGLE | SWT.RIGHT);
		delayField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		delayField.setText(String.valueOf(getDelayNanos()));
		delayField.clearSelection();
		delayField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {					
					setDelayNanos(Integer.parseInt(delayField.getText()));
				}
				catch (Exception err) {
					delayField.setText(String.valueOf(getDelayNanos()));
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
				// stopButton.setEnabled(true);
				// startButton.setEnabled(false);
				// resetButton.setEnabled(false);
				// stepButton.setEnabled(false);
				delayField.setEnabled(false);
				runnerThread = new Thread(createRunLoop());
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
		// stopButton.setEnabled(false);
		// startButton.setEnabled(true);
		// resetButton.setEnabled(true);
		// stepButton.setEnabled(true);
		delayField.setEnabled(true);
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

	private Runnable createRunLoop() {
		if (delayNanos == 0)
			return new RunLoopNoSleep();
		else
			// return new RunLoopSleepPerStep();
			return new RunLoopBudgetNanos();
	}
	
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
