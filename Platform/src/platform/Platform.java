package platform;

import java.util.Observable;
import java.util.Observer;
import platform.area.Area;
import platform.area.AreaException;
import platform.component.Coin;
import platform.component.terrain.Dirt;

/**
 *
 * @author richkent
 */
public class Platform implements Observer {
    public static final int GRAVITY = 5;
    public static Dimension blockSize = new Dimension(48, 48);
    public static Dimension spaceSize = new Dimension(200, 200);

    public static void main(String[] args) throws AreaException {
        new Platform();
        
        Area a = new Area(blockSize.times(10));
        for (int i = 0; i < 10; i++) {
            Dirt d = new Dirt(blockSize.times(0, i));
            a.addComponent(d);
            d = new Dirt(blockSize.times(9, i));
            a.addComponent(d);
            d = new Dirt(blockSize.times(i, 0));
            a.addComponent(d);
            d = new Dirt(blockSize.times(i, 9));
            a.addComponent(d);
        }
        
        Coin c = new Coin(blockSize.times(5, 2));
        a.addComponent(c);
        
        a.initialize();
        
        int msperframe = 100;
        for (int frame = 0; frame < 10; frame++) {
            System.out.println("Frame " + frame);
            a.moveAll(msperframe);
            System.out.println("  Coin position: " + c.position());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        
    }
}
