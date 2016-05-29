package platform;

import java.util.Observable;
import java.util.Observer;
import platform.area.Area;
import platform.area.AreaException;
import platform.collectible.Coin;
import platform.component.Component;
import platform.component.terrain.Dirt;

/**
 *
 * @author richkent
 */
public class Platform implements Observer {
    public static final int GRAVITY = 48;
    public static Dimension blockSize = new Dimension(48, 48);
    public static Dimension spaceSize = new Dimension(200, 200);

    public static void main(String[] args) throws AreaException {
        new Platform();
        
        Area a = new Area(blockSize.times(10));
        Dirt d;
        for (int i = 0; i < 10; i++) {
            d = new Dirt(blockSize.times(0, i));
            // a.addComponent(d);
            d = new Dirt(blockSize.times(9, i));
            // a.addComponent(d);
            d = new Dirt(blockSize.times(i, 0));
            // a.addComponent(d);
            d = new Dirt(blockSize.times(i, 9));
            a.addComponent(d);
        }
        
        Coin c1 = new Coin(blockSize.times(5, 2));
        a.addComponent(c1);
        Coin c2 = new Coin(blockSize.times(8, 4));
        a.addComponent(c2);
        
        a.initialize();
        
        int msperframe = 1000/30;
        for (int frame = 0; frame < 30; frame++) {
            //if (frame == 24) {
            //    a.debug(true);
            //} else {
                a.debug(false);
            //}
                
            // System.out.println("Frame " + frame);
            a.moveAll(msperframe);
            
            // System.out.println("  Coin 1 position: " + c1.position() + " speed: " + c1.speed());
            // System.out.println("  Coin 2 position: " + c2.position() + " speed: " + c2.speed());
        }
        
        for (Component comp : a.components()) {
            // System.out.println(comp.getClass().getSimpleName() + " " + comp.id() + " " + comp.position());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        
    }
}
