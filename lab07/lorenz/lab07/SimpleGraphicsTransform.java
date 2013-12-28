package lorenz.lab07;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A very simple transform from a 3-D Euclidean space to 2-D canvas pixels. The
 * canvas shows the space with the x-axis running horizontally (increasing to
 * the right) and the z-axis vertically (increasing upward).
 * 
 * @author jehanson
 * 
 */
public class SimpleGraphicsTransform {

	private static String clsName = SimpleGraphicsTransform.class.getName();
	private static Logger logger = Logger.getLogger(clsName);

	private double xFactor;
	private double xOffset;
	private double zFactor;
	private double zOffset;

	public SimpleGraphicsTransform() {
		xFactor = 1;
		xOffset = 0;
		zFactor = 1;
		zOffset = 0;
	}

	public void initialize(DataBox dataBounds, Rectangle drawArea) {
		final String mtdName = "initialize";
		logger.logp(Level.INFO, clsName, mtdName, "dataBounds=" + dataBounds
				+ " drawArea=" + drawArea);

		int a0 = drawArea.x;
		int a1 = drawArea.x + drawArea.width;
		int b0 = drawArea.y;
		int b1 = drawArea.y + drawArea.height;

		double x0 = dataBounds.getXMin();
		double x1 = dataBounds.getXMax();
		double z0 = dataBounds.getZMin();
		double z1 = dataBounds.getZMax();

		xFactor = (a1 - a0) / (x1 - x0);
		xOffset = a0 - xFactor * x0;

		zFactor = (b1 - b0) / (z0 - z1);
		zOffset = b0 - zFactor * z1;
	}

	public Point dataToGraphics(DataPoint dataPt) {
		final String mtdName = "dataToGraphics";

		int px = (int) (dataPt.getX() * xFactor + xOffset);
		int py = (int) (dataPt.getZ() * zFactor + zOffset);
		Point p = new Point(px, py);

		if (logger.isLoggable(Level.FINER))
			logger.logp(Level.FINER, clsName, mtdName, "" + dataPt + " => " + p);
		return p;
	}

}
