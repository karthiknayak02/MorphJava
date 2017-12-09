import java.awt.geom.*;

public class Triangle
{
    private Ellipse2D.Double[] tri2;

    public Triangle(controlPoint a, controlPoint b, controlPoint c)
    {
        tri2 = new Ellipse2D.Double[3];
        tri2[0] = a;
        tri2[1] = b;
        tri2[2] = c;
    }

    public double getX(int index)
    {
        if ((index >= 0) && (index < 3))
            return (tri2[index].x);
        System.out.println("Index out of bounds in getX()");
        return (0.0);
    }

    public double getY(int index)
    {
        if ((index >= 0) && (index < 3))
            return (tri2[index].y);
        System.out.println("Index out of bounds in getY()");
        return (0.0);
    }
}
