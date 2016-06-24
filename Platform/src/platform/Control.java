package platform;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;

/**
 *
 * @author richkent
 */
public class Control extends Observable implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { 
        setChanged();
        notifyObservers(new PlayerCommand(e, true));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setChanged();
        notifyObservers(new PlayerCommand(e, false));
    }
    
}
