package platform.collectible;

import java.util.ArrayList;
import platform.Dimension;
import platform.component.Collision;
import platform.component.CollisionResult;
import platform.component.Component;

/**
 *
 * @author richkent
 */
public abstract class Collectible extends Component {

    public Collectible(Dimension position) {
        super(position);
    }
    public abstract ArrayList<CollectResult> collect();
    @Override
    public ArrayList<CollisionResult> collide(Component c, Collision.CollisionType type) {
        return null;
    }
}
