package lorenz.lab10;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * 
 * @author jehanson
 */
public interface ViewDecorator {

	public void draw(GC gc, Rectangle clientArea, GraphicsTransform transform);
}
