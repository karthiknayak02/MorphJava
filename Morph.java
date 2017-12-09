/*********************
 * P4: Morph
 * Karthik Nayak & Steven Penava
 * Dr. Brent Seales
 * 12/09/2017
 *********************/

/* Imports */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/* Window holding boards */
public class Morph extends JFrame implements ActionListener {

    /* Initializations */
    private Board leftBoard, rightBoard;
    private Board previewBoard;

    private boolean isDragging;
    int size;
    private int[] mousePt  = null;
    private Polygon boundry;

    private static JPanel buttonHolder;
    private static JButton preview, reset, LL, LR, RL, RR;

    private static JCheckBox exportImagesCB;
    public static JLabel statusL, statusR;

    /* Menu */
    private JMenuBar menuBar;
    private JMenu menu, sizeMenu;
    private JMenuItem menuItem1, menuItem2, five, ten, twenty;

    Container c;

    /* File choosers */
    JFileChooser c1, c2;
    private float li = 1, ri = 1;

    /* Constructor */
    private Morph(int size){
        super("Morph");
        createMenu();

        this.size = size;
        leftBoard = new Board(size);
        rightBoard = new Board(size);


        preview = new JButton("Preview");
        reset = new JButton("Reset");
        LL = new JButton("<•");
        LR = new JButton("•>");
        RL = new JButton("•<");
        RR = new JButton(">•");
        exportImagesCB = new JCheckBox("Export Images?");

        preview.addActionListener(this);
        reset.addActionListener(this);
        LL.addActionListener(this);
        LR.addActionListener(this);
        RL.addActionListener(this);
        RR.addActionListener(this);
        five.addActionListener(this);
        ten.addActionListener(this);
        twenty.addActionListener(this);

        LL.setEnabled(false);
        LR.setEnabled(false);
        RL.setEnabled(false);
        RR.setEnabled(false);


        statusL = new JLabel("Start Image");
        statusR = new JLabel("End Image");

        buttonHolder = new JPanel();
        buttonHolder.add(LL);
        buttonHolder.add(LR);
        buttonHolder.add(statusL);
        buttonHolder.add(preview);
        buttonHolder.add(reset);
        buttonHolder.add(statusR);
        buttonHolder.add(RL);
        buttonHolder.add(RR);
        buttonHolder.add(exportImagesCB);

        leftBoard.setDoubleBuffered(true);
        rightBoard.setDoubleBuffered(true);

        /* Mouse click/release detection */
        createMouseListeners(leftBoard, rightBoard);
        createMouseListeners(rightBoard, leftBoard);

        c = getContentPane();

        c.add(leftBoard, BorderLayout.WEST);
        c.add(rightBoard, BorderLayout.EAST);
        c.add(buttonHolder, BorderLayout.SOUTH);

        setSize(950, 555);
        setResizable(false);
        setVisible(true);
    }

    private void createMenu(){
        five = new JMenuItem("5x5");
        ten = new JMenuItem("10x10");
        twenty = new JMenuItem("20x20");

        /* file choosers */
        c1 = new JFileChooser();
        c2 = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG/JPG Images", "jpg", "jpeg");
        c1.setFileFilter(filter);
        c2.setFileFilter(filter);

        menuBar = new JMenuBar();
        menu = new JMenu("File");
        sizeMenu = new JMenu("Grid Size");

        /* image 1 loading */
        menuItem1 = new JMenuItem("Load Image 1...", KeyEvent.VK_T);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = c1.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    if (leftBoard.setImage(c1.getSelectedFile().getPath())) {
                        LL.setEnabled(true);
                        LR.setEnabled(true);
                    }
                }
            }
        });

        /* image 2 loading */
        menuItem2 = new JMenuItem("Load Image 2...", KeyEvent.VK_T);
        menuItem2.setAccelerator(KeyStroke.getKeyStroke('2', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        menuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = c2.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    if (rightBoard.setImage(c2.getSelectedFile().getPath())) {
                        RL.setEnabled(true);
                        RR.setEnabled(true);
                    }
                }
            }
        });

        menu.add(menuItem1);
        menu.add(menuItem2);
        sizeMenu.add(five);
        sizeMenu.add(ten);
        sizeMenu.add(twenty);
        menuBar.add(menu);
        menuBar.add(sizeMenu);

        super.setJMenuBar(menuBar);
    }

    public void actionPerformed(ActionEvent e){

        if (e.getActionCommand().equals("Preview")) {
            previewBoard = new Board(size);

          try{
                previewBoard.setImageCustom(c1.getSelectedFile().getPath(), c2.getSelectedFile().getPath());
                previewBoard.generateAnimated(leftBoard, rightBoard, exportImagesCB.isSelected());

                JFrame animateFrame = new JFrame("Preview");

                animateFrame.add(previewBoard);
                animateFrame.setSize(leftBoard.img.getWidth()+5, leftBoard.img.getHeight()+10);
                animateFrame.setVisible(true);
          }
            catch (NullPointerException n) {}
        }

        if (e.getActionCommand().equals("Reset")) {
            leftBoard.reset();
            rightBoard.reset();
        }

        else {
            String cmd = e.getActionCommand();
            if (cmd == "<•") leftBoard.setIntensity(li -= 0.1);
            if (cmd == "•>") leftBoard.setIntensity(li += 0.1);
            if (cmd == "•<") rightBoard.setIntensity(ri -= 0.1);
            if (cmd == ">•") rightBoard.setIntensity(ri += 0.1);

            if (cmd == "5x5") {
                dispose();
                size = 5;
                Morph M2 = new Morph(size);
                //M2.setSize(new Dimension(850, 505));
                M2.setResizable(false);
                M2.addWindowListener(new WindowAdapter(){
                    public void windowClosing(WindowEvent e){System.exit(0);}
                });


            }
            if (cmd == "10x10") {
                dispose(); //resetEverything(5);
                size = 10;
                Morph M2 = new Morph(size);
                M2.setSize(new Dimension(950, 555));
                M2.setResizable(false);
                M2.addWindowListener(new WindowAdapter(){
                    public void windowClosing(WindowEvent e){System.exit(0);}
                });
            }
            if (cmd == "20x20") {
                dispose(); //resetEverything(5);
                size = 20;
                Morph M2 = new Morph(size);
                M2.setSize(new Dimension(1000, 580));
                M2.setResizable(false);
                M2.addWindowListener(new WindowAdapter(){
                    public void windowClosing(WindowEvent e){System.exit(0);}
                });
            }

            leftBoard.repaint();
            rightBoard.repaint();
        }

    }

    public static void main(String args[]){

        Morph M = new Morph(10);
        M.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){System.exit(0);}
        });
    }

    public void createMouseListeners(Board leftBoard, Board rightBoard) {

        leftBoard.addMouseListener(new MouseListener(){
            public void mouseExited(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseReleased(MouseEvent e){
                if (mousePt != null) {
                    leftBoard.setTriangles();
                    rightBoard.points[mousePt[0]][mousePt[1]].setColor(Color.BLACK);
                    rightBoard.repaint();
                }
                isDragging = false;
                mousePt = null;


            }
            public void mousePressed(MouseEvent e){
                mousePt = leftBoard.getPoint(e.getPoint());
                if (mousePt != null) {
                    isDragging = true;
                    boundry = leftBoard.createPoly(mousePt[0], mousePt[1]);
                    rightBoard.points[mousePt[0]][mousePt[1]].setColor(Color.RED);
                    rightBoard.revalidate();
                    rightBoard.repaint();
                }
            }
            public void mouseClicked(MouseEvent e){}
        });

        /* create a motion listener to track the mouse dragging the polygon */
        leftBoard.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                if (isDragging && boundry.contains(e.getPoint())){
                    leftBoard.points[mousePt[0]][mousePt[1]] = new controlPoint(e.getX(), e.getY());
                    rightBoard.points[mousePt[0]][mousePt[1]].setColor(Color.RED);                      // remove
                    rightBoard.repaint();
                    leftBoard.repaint();
                }
            }
            public void mouseMoved(MouseEvent e) { }
        });

    }
}