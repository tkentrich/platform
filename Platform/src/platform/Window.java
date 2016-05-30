package platform;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author richkent
 */
public class Window extends JFrame {
    public Window() {
        super();

        setUndecorated(true);
        setVisible(true);
        //setBounds(200, 0, 800, 800);
        setExtendedState(getExtendedState()|MAXIMIZED_BOTH);
        
        getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
            new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor"));

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
}
