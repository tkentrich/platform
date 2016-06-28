package platform;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Observable;

/**
 *
 * @author richkent
 */
public class Control extends Observable implements KeyListener {

    private HashMap<Integer, Boolean> state;
    
    public Control() {
        state = new HashMap();
    }
    
    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!state.containsKey(e.getKeyCode()) || state.get(e.getKeyCode()) == false) {
            state.put(e.getKeyCode(), true);
            setChanged();
            notifyObservers(new PlayerCommand(e, true));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!state.containsKey(e.getKeyCode()) || state.get(e.getKeyCode()) == true) {
            state.put(e.getKeyCode(), false);
            setChanged();
            notifyObservers(new PlayerCommand(e, false));
        }
    }
    
}
