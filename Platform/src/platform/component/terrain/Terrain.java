package platform.component.terrain;

import java.util.ArrayList;
import platform.Dimension;
import platform.component.Collision.CollisionType;
import platform.component.CollisionResult;
import platform.component.Component;

/**
 *
 * @author richkent
 */
public abstract class Terrain extends Component {

    public Terrain(Dimension position) {
        super(position);
    }
    public boolean passable() {
        return false;
    }
    public int weight() {
        return 0;
    }
    public ArrayList<CollisionResult> collide(Component c, CollisionType type) { 
        return null;
    }
    public double frictionFactor() {
        return 1;
    }
    public abstract ArrayList<WalkModifier> walkModifiers();
}
