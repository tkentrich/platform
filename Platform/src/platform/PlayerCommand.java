package platform;

import java.awt.event.KeyEvent;

/**
 *
 * @author richkent
 */
public class PlayerCommand {
    private final KeyEvent ev;
    private final boolean typed;
    
    public PlayerCommand(KeyEvent event, boolean wasTyped) {
        ev = event;
        typed = wasTyped;
        System.out.println(this);
    }
    public KeyEvent event() {
        return ev;
    }
    public boolean typed() {
        return typed;
    }
    public String toString() {
        return String.format("%d (%s) %s", ev.getKeyCode(), ev.getKeyChar(), typed ? "Pressed" : "Released");
    }
}
