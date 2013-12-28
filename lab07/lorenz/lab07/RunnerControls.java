package lorenz.lab07;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

public class RunnerControls {

	private static final String clsName = RunnerControls.class.getName();
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
			
			long t0, sleepTime;
			while (runnerThread == Thread.currentThread()) {
				t0 = System.currentTimeMillis();
				steppable.step();
				sleepTime = stepMillis - (System.currentTimeMillis()-t0);
				if (sleepTime > 0) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// reset interrupted flag and exit run-loop
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
			if (logger.isLoggable(Level.FINE))
				logger.logp(Level.FINE, clsName, mtdName, "exiting");
		}

	}

	// ======================================
	// Variables
	// ======================================

	private final Steppable steppable;
	private long stepMillis;
	private final Object runnerGuard = new Object();
	private volatile Thread runnerThread;
	private Image startImage;
	private Image stopImage;
	private Image stepImage;
	private Image resetImage;

	// ======================================
	// Creation
	// ======================================

	public RunnerControls(Steppable steppable) {
		this.steppable = steppable;
		this.stepMillis = 1;
	}

	// ======================================
	// Operation
	// ======================================

	public long getStepMillis() {
		return this.stepMillis;
	}

	public void setStepMillis(long stepMillis) {
		if (!(stepMillis >= 0))
			throw new IllegalArgumentException("stepMillis must be > 0");
		this.stepMillis = stepMillis;
	}

	public Control buildControls(Composite parent) {
		Composite cpane = new Composite(parent, SWT.BORDER);
		cpane.setLayout(new GridLayout(4, true));

		Display display = parent.getDisplay();

		resetImage = loadImage(display, "resources/reset.gif");
		startImage = loadImage(display, "resources/start.gif");
		stepImage = loadImage(display, "resources/step.gif");
		stopImage = loadImage(display, "resources/stop.gif");

		Button resetButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		resetButton.setImage(resetImage);
		resetButton.setToolTipText("reset");
		resetButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				reset();
			}
		});

		Button startButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		startButton.setImage(startImage);
		startButton.setToolTipText("start");
		startButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				start();
			}
		});

		Button stepButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		stepButton.setImage(stepImage);
		stepButton.setToolTipText("step");
		stepButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,
				1, 1));
		stepButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				step();
			}
		});

		Button stopButton = new Button(cpane, SWT.PUSH | SWT.FLAT);
		stopButton.setImage(stopImage);
		stopButton.setToolTipText("stop");
		stopButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,
				1, 1));
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stop();
			}
		});

		Label stepMillisLabel = new Label(cpane, SWT.RIGHT);
		stepMillisLabel.setText("delay per step (millisec):");
		stepMillisLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				3, 1));
		
		final Text stepMillisText = new Text(cpane, SWT.SINGLE | SWT.RIGHT);
		stepMillisText.setText(String.valueOf(getStepMillis()));
		stepMillisText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		stepMillisText.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {
					setStepMillis(Integer.parseInt(stepMillisText.getText()));
				}
				catch (Exception err) {
					stepMillisText.setText(String.valueOf(getStepMillis()));
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
			logger.logp(Level.FINE, clsName,  mtdName, "starting");
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
			logger.logp(Level.FINE, clsName,  mtdName, "stopping");
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
		synchronized (runnerGuard) {
			if (runnerThread == null)
				steppable.reset();
		}
	}

	// =================================
	// Private
	// =================================

	private Image loadImage(Display display, String fileName) {
		final String mtdName = "loadImage";
		Image image = null;
		try {
			URL url = RunnerControls.class.getResource(fileName);
			if (logger.isLoggable(Level.FINE))
				logger.logp(Level.FINE, clsName, mtdName, "image url: " + url);
			InputStream is = url.openStream();
			image = new Image(display, is);
			is.close();
		} catch (IOException e) {
			// Ignore
		}
		return image;
	}

}
