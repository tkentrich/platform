package platform;

import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static platform.Platform.blockSize;
import platform.area.Area;
import platform.area.AreaException;
import platform.collectible.Coin;
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
    public static boolean debug;
    
    public static void main(String[] args) throws AreaException {
        new Platform();
    }

    public Platform() {
        Window w = new Window();
        v = new CanvasViewer();
        w.add(v);
        Control c = new Control();
        c.addObserver(this);
        w.addKeyListener(c);

        currentArea = new Area(blockSize.times(10));
        currentArea.addObserver(this);
        
        Player p = new Player(blockSize.times(6, 4));
        //p.push(blockSize.times(100, -500));
        currentArea.addComponent(p);
        
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
        Coin c2 = new Coin(blockSize.times(8, 4), new Dimension(200, 100));
        currentArea.addComponent(c1);
        currentArea.addComponent(c2);
        
        try {
            currentArea.initialize();
        } catch (AreaException ex) {
            Logger.getLogger(Platform.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int msperframe = 1000/15;
        //for (int frame = 0; frame < 60; frame++) {
        while (true) {
            long startTime = System.currentTimeMillis();
            currentArea.moveAll(msperframe);
            long timeToSleep = 100 - (System.currentTimeMillis() - startTime);
            if (timeToSleep > 0) {
                try {
                
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Platform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Control) {
            if (arg instanceof PlayerCommand) {
                PlayerCommand comm = (PlayerCommand)arg;
                switch (comm.event().getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_CONTROL: // FIRE
                    case KeyEvent.VK_SPACE:   // JUMP
                    case KeyEvent.VK_SHIFT:   // RUN
                    case KeyEvent.VK_ALT:     // ???
                        currentArea.player().ui(comm);
                        break;
                    case KeyEvent.VK_Q:
                        System.exit(0);
                    case KeyEvent.VK_D:
                        debug = !debug;
                }
            }
        }
        v.paintArea(currentArea);
    }
        
}
