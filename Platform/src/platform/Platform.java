package platform;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author richkent
 */
public class Platform implements Observer {
    public static final int GRAVITY = 5;
    public static Dimension blockSize = new Dimension(48, 48);
    public static Dimension spaceSize = new Dimension(200, 200);

    public static void main(String[] args) {
        new Platform();
    }

    @Override
    public void update(Observable o, Object arg) {
        
    }
}
