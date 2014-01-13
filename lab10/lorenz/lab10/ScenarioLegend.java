package lorenz.lab10;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * 
 * @author jehanson
 */
public abstract class ScenarioLegend implements ViewDecorator {

	private static final String clsName = ScenarioLegend.class.getName();
	private static final Logger logger = Logger.getLogger(clsName);

	public abstract String[] getCoordinateLabels();

	public abstract String getTimeLabel();

	@Override
	public void draw(GC gc, Rectangle clientArea, GraphicsTransform transform) {
		final String mtdName = "draw";
		if (logger.isLoggable(Level.FINE))
			logger.logp(Level.FINE, clsName, mtdName, "entering. clientArea="
					+ clientArea);

		// current time
		int x = clientArea.x + 12;
		int y = clientArea.y + 8;
		Color oldFG = gc.getForeground();
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		gc.drawText(getTimeLabel(), x, y);
		
		hackTheAxisGlyph(gc, clientArea, transform);
		
		gc.setForeground(oldFG);
	}

	/**
	 * EVIL HACKERY to get me started....
	 * 
	 * @param gc
	 * @param clientArea
	 * @param transform
	 */
	private void hackTheAxisGlyph(GC gc, Rectangle clientArea, GraphicsTransform transform) {
		// final String mtdName = "hackTheAxisGlyph";
		if (transform instanceof SimpleGraphicsTransform) {
			String[] axisLabels = getCoordinateLabels();
			int originX = clientArea.x + 10;
			int originY = clientArea.y + clientArea.height - 10;
			
			int xTipX = originX + 32;
			int xTipY = originY;
			String xLabel = axisLabels[0]; 
			Point xExtent = gc.textExtent(xLabel);
			gc.drawLine(originX, originY, xTipX, xTipY);
			gc.drawLine(xTipX, xTipY, xTipX-5, xTipY-3);
			gc.drawLine(xTipX, xTipY, xTipX-5, xTipY+3);
			gc.drawText(xLabel, xTipX+2, xTipY-xExtent.y-2);
			
			int zTipY = originY - 32;
			int zTipX = originX;
			final String zLabel = axisLabels[2];
			Point zExtent = gc.textExtent(zLabel);
			gc.drawLine(originX, originY, zTipX, zTipY);
			gc.drawLine(zTipX, zTipY, zTipX-3, zTipY+5);
			gc.drawLine(zTipX, zTipY, zTipX+3, zTipY+5);
			gc.drawText(zLabel, zTipX+2, zTipY-zExtent.y-2);
		}
	}

}
