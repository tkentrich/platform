package platform.collectible;

import platform.component.CollisionResult;

/**
 *
 * @author richkent
 */
public class Collect extends CollisionResult {
    private Collectible c;
    public Collect(Collectible c) {
        this.c = c;
    }
    public Collectible collectible() {
        return c;
    }
}
