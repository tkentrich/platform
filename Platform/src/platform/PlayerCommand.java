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
    }
    public KeyEvent event() {
        return ev;
    }
    public boolean typed() {
        return typed;
    }
}
