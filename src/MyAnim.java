import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Scanner;
import javax.swing.*;

/**
 * The MyAnim class creates a graphical animation of two dancing letters (Z and C).
 * The letters move across the screen, bounce off boundaries, rotate, and change size.
 * It uses a Swing Timer to handle the animation frames and Graphics2D for rendering.
 */
public class MyAnim extends JPanel implements ActionListener {
    /** Polygons representing the shapes of letters Z and C. */
    private   Polygon polyZ, polyC;

    // מיקום התחלתי ומהירות
    /** Current X and Y position coordinates of the letters. */
    private int x = 50, y = 50;
    /** Current velocity (speed and direction) on the X and Y axes. */
    private int velX = 2, velY = 2;

    // הגדרת גבולות של "זירת הריקוד" - כשיגיעו לקצוות האלה הם יזוזו בכיוון אחר
    private final int BOX_X = 20;
    private  final int BOX_Y = 20;
    private final int BOX_WIDTH = 550;
    private final int BOX_HEIGHT = 450;

    // משתני אנימציה
    private double angle = 0;
    private double scale = 1.0;
    private double scaleStep = 0.01; //קצב שינוי גודל בכל פריים

    //צבעים התחלתים שלנו:)
    private  Color colorZ = Color.PINK;
    private Color colorC = Color.BLUE;

    private  Timer timer = new Timer(15, this);

    /**
     * Constructs the animation panel, loads letter shapes from files,
     * and starts the animation timer.
     */
    public MyAnim() {
        //טעינת אותיות מהקבצים
        polyZ = loadPolygonFromFile("Z.txt");
        polyC = loadPolygonFromFile("C.txt");
        timer.start();
    }

    /**
     * Reads coordinates from a text file and builds a Polygon object.
     * Expected file format: "x,y" per line.
     * @param fileName The name or path of the file to load.
     * @return A Polygon containing the points from the file.
     */
    public Polygon loadPolygonFromFile(String fileName) {
        Polygon p = new Polygon();
        try {
            Scanner s = new Scanner(new File(fileName));
            while (s.hasNextLine()) {
                String[] parts = s.nextLine().split(",");
                p.addPoint(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
            }
            s.close();
        } catch (Exception e) {
            System.err.println("Error loading file: " + fileName);
        }
        return p;
    }
    /**
     * Updates the animation state on every timer tick.
     * Handles movement, rotation increment, scaling, and boundary collision detection.
     * @param e The ActionEvent triggered by the Timer.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // קידום המיקום לפי המהירות הנוכחית
        x += velX;
        y += velY;

        // אנימציה
        angle += 0.04;
        scale += scaleStep;
        if (scale > 1.2 || scale < 0.7) // הגענו למקסימום נקטין הגענו למינימום נגדיל
            scaleStep = -scaleStep;

        // בדיקת גבולות
        // הרוחב הכולל של Z וC הוא 270PX
        if (x < BOX_X || x > BOX_X + BOX_WIDTH - 270) {
            velX = -velX;
            changeColors(); // שינוי צבע כשנתקרב לגבולות שהגדרנו
            if (x < BOX_X) x = BOX_X;
            if (x > BOX_X + BOX_WIDTH - 270) x = BOX_X + BOX_WIDTH - 270;
        }
        // גובה של 2 האותיות 200PX
        if (y < BOX_Y || y > BOX_Y + BOX_HEIGHT - 200) {
            velY = -velY;
            changeColors();
            if (y < BOX_Y) y = BOX_Y;
            if (y > BOX_Y + BOX_HEIGHT - 200) y = BOX_Y + BOX_HEIGHT - 200;
        }
        repaint(); // קריאה לצייר מחדש את המסך
    }
    /**
     * Changes the colors of both letters to random RGB values upon collision.
     */
    private void changeColors() {
        colorZ = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
        colorC = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }

    /**
     * Renders the current state of the animation to the screen.
     * Uses {@link Graphics2D} to perform transformations like translate, rotate, and scale.
     * @param g The Graphics context to paint on.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // שיניתי רקע לשחור כי זה נראה לי יותר יפה
        // אם זה מפריע לך נחזור לאפור
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // ציור Z
        Graphics2D gZ = (Graphics2D) g2.create();
        gZ.translate(x, y);// קביעת מיקום האות במסך
        gZ.rotate(angle, 100, 125);// סיבוב סביב מרכז האות
        gZ.scale(scale, scale);
        gZ.setColor(colorZ);
        gZ.fillPolygon(polyZ);
        gZ.dispose();// שחרור העותק וחזרה להגדרות מקוריות

        // ציור C
        Graphics2D gC = (Graphics2D) g2.create();
        gC.translate(x, y);
        gC.rotate(angle, 225, 125); // סיבוב סביב מרכז האות
        gC.scale(scale, scale);
        gC.setColor(colorC);
        gC.fillPolygon(polyC);
        gC.dispose();
    }
    /**
     * Main method to set up the JFrame and host the animation panel.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        JFrame f = new JFrame("Z & C Dancing Logo");
        f.add(new MyAnim());
        f.setSize(650, 550);
        f.setResizable(false);// נעילה של גודל חלונית כדי לשמור על הגבולות
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}