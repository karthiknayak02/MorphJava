/*********************
 * P4: Morph
 * Karthik Nayak & Steven Penava
 * Dr. Brent Seales
 * 12/09/2017
*********************/

/* Imports */
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Line2D;
import java.awt.event.*;
import javax.swing.Timer;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/* Individual frame for holding each image */
public class Board extends JPanel
{
    /* Initializations */
    public controlPoint points[][];
    public Triangle triangles[][][];
    public BufferedImage img = null, original = null;
    public BufferedImage img1 = null, img2 = null;

    private MorphTools mt = new MorphTools();
    private AlphaComposite ac1, ac2;

    private Timer previewTimer;
    private double tStep = 0, speed = 5;

    private int size, numFrames = 0;;
    private int[][] adj = { {-1, -1} , {1, 0} , {1, 1} , {0, 1} , {-1, 0} , {-1, -1} };

    /* Constructor */
    public Board(int a)
    {
        /* Setting size based on grid size */
        this.size = a;
        if (this.size == 5) {
            super.setPreferredSize(new Dimension(425, 400));
        }
        if (this.size == 10) {
            super.setPreferredSize(new Dimension(475, 500));
        }
        if (this.size == 20) {
            super.setPreferredSize(new Dimension(498, 500));
        }

        /* Design */
        super.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.WHITE));

        /* Creating control points */
        points = new controlPoint[size][size];
        int box = 500/size;
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                int x = i * box + 5;
                int y = j * box + 5;
                points[i][j] = new controlPoint(x, y);
            }
        }

        /* Creating triangles for warping */
        triangles = new Triangle[size -1][size -1][2];
        for (int i = 0; i < size -1; i++){
            for (int j = 0; j < size -1; j++){
                triangles[i][j][0] = new Triangle(points[i][j], points[i+1][j], points[i+1][j+1]);
                triangles[i][j][1] = new Triangle(points[i][j], points[i][j+1], points[i+1][j+1]);
            }
        }
    }

    /* Painting lines, points, images */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        if (img1 != null && img2 != null){
            try {
                g2d.setComposite(ac1);
                g2d.drawImage(img1, 0, 0, null);
                g2d.setComposite(ac2);
                g2d.drawImage(img2, 0, 0, null);
            }
            catch (IllegalArgumentException i) {}
        }
        else {
            g.drawImage(img, 0, 0, null);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    g2d.setColor(Color.BLACK);

                    /* Drawing lines to form triangles */
                    if (i + 1 < size)
                        g2d.draw(new Line2D.Double( points[i][j].x + 5, points[i][j].y + 5, points[i + 1][j    ].x + 5, points[i + 1][j    ].y + 5));   // east

                    if (i + 1 < size && j + 1 < size)
                        g2d.draw(new Line2D.Double( points[i][j].x + 5, points[i][j].y + 5, points[i + 1][j + 1].x + 5, points[i + 1][j + 1].y + 5));   // south east

                    if (j + 1 < size)
                        g2d.draw(new Line2D.Double( points[i][j].x + 5, points[i][j].y + 5, points[i    ][j + 1].x + 5, points[i    ][j + 1].y + 5));   // south

                    g2d.setColor(points[i][j].getColor());
                    g2d.fill(points[i][j]);
                }
            }
        }
    }

    /* Border polygons */
    public Polygon createPoly(int x, int y)
    {
        List<Double> xPts = new ArrayList<>();
        List<Double> yPts = new ArrayList<>();

        for (int q = 0; q < adj.length; q++){
            x += adj[q][0];
            y += adj[q][1];
            if (!(x < 0 || x >= size || y < 0 || y >= size)) {
                xPts.add(points[x][y].x);
                yPts.add(points[x][y].y);
            }
        }

        int[] xPoints = new int[xPts.size()];
        int[] yPoints = new int[yPts.size()];

        for (int i=0; i < xPoints.length; i++)
        {
            xPoints[i] = xPts.get(i).intValue();
            yPoints[i] = yPts.get(i).intValue();
        }

        return new Polygon( xPoints, yPoints, xPoints.length);
    }

    /* Return clicked point */
    public int[] getPoint(Point p)
    {
        for (int i = 1; i < size - 1; i++) {
            for (int j = 1; j < size - 1; j++) {
                if (points[i][j].contains(p.x, p.y)) {
                    int[] mousePt = {i, j};
                    return mousePt;
                }
            }
        }
        return null;
    }

    /* Morph animation */
    public void generateAnimated(Board left, Board right, boolean exportImages) {

        ActionListener seconds = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (step(left.points, right.points, tStep)) {
                    previewTimer.stop();
                }
                else {
                    tStep += 0.006666667;
                    for (int i = 0; i < left.triangles.length; i++) {
                            for (int j = 0; j < left.triangles[0].length; j++) {
                                mt.warpTriangle(left.img, img1, left.triangles[i][j][0], triangles[i][j][0], null, null);
                                mt.warpTriangle(left.img, img1, left.triangles[i][j][1], triangles[i][j][1], null, null);

                                mt.warpTriangle(right.img, img2, right.triangles[i][j][0], triangles[i][j][0], null, null);
                                mt.warpTriangle(right.img, img2, right.triangles[i][j][1], triangles[i][j][1], null, null);

                                img1.createGraphics();
                                if (tStep >= 1) tStep =1;
                                ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1-tStep));

                                img2.createGraphics();
                                ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (tStep));
                                if (tStep == 1) tStep++;
                            }
                    }

                    repaint();

                    numFrames++;
                    if (exportImages) {
                        BufferedImage image = (BufferedImage)createImage(img1.getWidth(), img1.getHeight());
                        paint(image.getGraphics());
                        try{
                            File f = new File("output_" + numFrames + ".jpeg");
                            ImageIO.write(image, "jpeg", f);
                        }
                        catch (IOException i){ System.out.println("Had trouble writing the image."); }
                    }
                }
            }
        };

        previewTimer = new Timer(33, seconds);
        previewTimer.start();

    }

    /* Step function */
    public boolean step(controlPoint left[][], controlPoint right[][], double t) {

        boolean animateComplete = true;

        for (int i = 1; i < size - 1; i++) {
            for (int j = 1; j < size - 1; j++) {

                if (tStep <= 1) {

                    double newX, newY;

                    newX = left[i][j].x + t*(right[i][j].x - left[i][j].x);
                    newY = left[i][j].y + t*(right[i][j].y - left[i][j].y);

                    points[i][j].setLocation(newX, newY);

                    animateComplete = false;
                }
            }
        }

        return animateComplete;
    }

    /* Reset board */
    public void reset()
    {
        if (this.size == 5) {
            super.setPreferredSize(new Dimension(425, 400));
        }
        if (this.size == 10) {
            super.setPreferredSize(new Dimension(475, 500));
        }
        if (this.size == 20) {
            super.setPreferredSize(new Dimension(498, 500));
        }

        /* Creating control points */
        int box = 500/size;
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                int x = i * box + 5;
                int y = j * box + 5;
                points[i][j].setLocation(x, y);
            }
        }

        /* Creating triangles for warping */
        triangles = new Triangle[size -1][size -1][2];
        for (int i = 0; i < size -1; i++){
            for (int j = 0; j < size -1; j++){
                triangles[i][j][0] = new Triangle(points[i][j], points[i+1][j], points[i+1][j+1]);
                triangles[i][j][1] = new Triangle(points[i][j], points[i][j+1], points[i+1][j+1]);
            }
        }

        numFrames = 0;
        repaint();
    }

    /* Setting triangles */
    public void setTriangles()
    {
        for (int i = 0; i < size -1; i++) {
            for (int j = 0; j < size -1; j++) {
                triangles[i][j][0] = new Triangle(points[i][j], points[i+1][j], points[i+1][j+1]);
                triangles[i][j][1] = new Triangle(points[i][j], points[i][j+1], points[i+1][j+1]);
            }
        }
    }

    /* opening image */
    public boolean setImage(String path) {
        try {
            img = ImageIO.read(new File(path));
            original = img;
            super.removeAll();
            super.revalidate();
            super.repaint();
            return true;

        } catch (IOException | NullPointerException no) {
            System.out.println("Error opening this file");
            return false;
        }
    }

    public void setImageCustom(String path1, String path2) {
        try {
            img1 = ImageIO.read(new File(path1));
            img2 = ImageIO.read(new File(path2));
            super.removeAll();
            super.revalidate();
            super.repaint();

        } catch (IOException | NullPointerException no) {
            System.out.println("Error opening this file");
        }
    }

    /* Handling brightness changes */
    public void setIntensity(float i)
    {
        RescaleOp op = new RescaleOp(i, 0, null);
        img = op.filter(original, null);
        repaint();
    }
}
