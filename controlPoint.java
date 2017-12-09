/*********************
 * P4: Morph
 * Karthik Nayak & Steven Penava
 * Dr. Brent Seales
 * 12/09/2017
 *********************/

/* Imports */
import java.awt.geom.Ellipse2D;
import java.awt.*;

/* Individual points on board */
public class controlPoint extends Ellipse2D.Double {

    private Color ptColor;

    public controlPoint(double x, double y) {
        height = 10;
        width = 10;
        this.x = x;
        this.y = y;

        ptColor = Color.BLACK;
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setColor(Color c) { ptColor = c; }

    public Color getColor() { return ptColor; }
}
