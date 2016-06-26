package platform.component.shot;

import platform.component.CollisionResult;
import platform.component.Component;

/**
 *
 * @author richkent
 */
public class ShotDamage extends CollisionResult {
    private final int damage;
    private final Component comp;
    public ShotDamage(Component comp, int damage) {
        this.comp = comp;
        this.damage = damage;
    }
    public int damage() {
        return damage;
    }
    public Component component() {
        return comp;
    }
}
