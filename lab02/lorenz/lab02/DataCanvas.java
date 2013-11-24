package lorenz.lab02;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * A canvas that can transform between data coordinates (described by DataPoints) and
 * canvas coordinates (described by pixel positions).
 * 
 * @author jehanson
 *
 */
public class DataCanvas extends Canvas {

	// ===========================
	// Inner classes
	// ===========================

	private class CListener implements ControlListener {

		@Override
		public void controlMoved(ControlEvent e) {}

		@Override
		public void controlResized(ControlEvent e) {
			// TODO Auto-generated method stub
		}	
	}
	
	// ===========================
	// Variables
	// ===========================

	// ===========================
	// Creation
	// ===========================

	public DataCanvas(Composite parent, int style) {
		super(parent, style);
		
		CListener clistener = new CListener();
		parent.addControlListener(clistener);
	}

	// ===========================
	// Operation
	// ===========================

	public void dataToCanvas(DataPoint dataPoint, Point canvasPoint) {
		
	}
	
}
