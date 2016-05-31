package platform;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static platform.Platform.blockSize;
import platform.area.Area;
import platform.area.AreaException;
import platform.collectible.Coin;
import platform.component.Component;
import platform.component.Player;
import platform.component.terrain.Dirt;

/**
 *
 * @author richkent
 */
public class Platform implements Observer {
    public static Dimension blockSize = new Dimension(48, 48);
    public static Dimension spaceSize = new Dimension(200, 200);
    public static final int GRAVITY = blockSize.y() * 20;
    
    private CanvasViewer v;
    private Area currentArea;

    public static void main(String[] args) throws AreaException {
        new Platform();
    }

    public Platform() {
        Window w = new Window();
        v = new CanvasViewer();
        w.add(v);
        
        currentArea = new Area(blockSize.times(10));
        currentArea.addObserver(this);
        Dirt d;
        for (int i = 0; i < 10; i++) {
            d = new Dirt(blockSize.times(0, i));
            currentArea.addComponent(d);
            d = new Dirt(blockSize.times(9, i));
            currentArea.addComponent(d);
            d = new Dirt(blockSize.times(i, 0));
            currentArea.addComponent(d);
            d = new Dirt(blockSize.times(i, 9));
            currentArea.addComponent(d);
        }
        
        Coin c1 = new Coin(blockSize.times(5, 2), new Dimension(-200, 50));
        currentArea.addComponent(c1);
        Coin c2 = new Coin(blockSize.times(8, 4), new Dimension(200, 100));
        currentArea.addComponent(c2);
        
        //Player p = new Player(blockSize.times(6, 1));
        //p.push(blockSize.times(100, -500));
        Player p = new Player(blockSize.times(6, 4));
        p.push(blockSize.times(100, -500));
        
        currentArea.addComponent(p);
        
        try {
            currentArea.initialize();
        } catch (AreaException ex) {
            Logger.getLogger(Platform.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int msperframe = 1000/15;
        for (int frame = 0; frame < 60; frame++) {
            long startTime = System.currentTimeMillis();
            // System.out.println("1 " + p.info());
                
            currentArea.moveAll(msperframe);
            // System.out.println("2 " + p.info());

            System.out.printf("  Frame %d (%dms) took %dms to process%n", frame, msperframe, System.currentTimeMillis() - startTime);
            long timeToSleep = 100 - (System.currentTimeMillis() - startTime);
            if (timeToSleep > 0) {
                try {
                
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Platform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            /* System.out.println("  Coin 1 position: " + c1.position() + " speed: " + c1.speed());
            System.out.println("  Coin 2 position: " + c2.position() + " speed: " + c2.speed());
            System.out.println("  Player position: " + p.position() + " speed: " + p.speed()); */
            System.out.println(c1.info());
        }
        //for (Component c : currentArea.components()) {
        //    System.out.println(c.info());
        //}
        System.exit(0);
    }

    @Override
    public void update(Observable o, Object arg) {
        v.paintArea(currentArea);
    }
        
}
