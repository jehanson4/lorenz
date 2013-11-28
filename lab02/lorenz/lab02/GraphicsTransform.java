package lorenz.lab02;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Maps data points (in 3-d Euclidean space) onto canvas points (2-d pixel
 * locations).
 * 
 * @author jehanson
 * 
 */
public interface GraphicsTransform {

	public void initialize(DataBox dataBounds, Rectangle drawArea);

	public Point dataToGraphics(DataPoint dataPt);

}
