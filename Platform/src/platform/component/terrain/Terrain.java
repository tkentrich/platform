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
    @Override
    public boolean passable() {
        return false;
    }
    @Override
    public int weight() {
        return 0;
    }
    @Override
    public ArrayList<CollisionResult> collide(Component c, CollisionType type) { 
        return new ArrayList();
    }
    @Override
    public double frictionFactor() {
        return 1;
    }
    @Override
    public boolean active() {
        return true;
    }
    public abstract ArrayList<WalkModifier> walkModifiers();
}
