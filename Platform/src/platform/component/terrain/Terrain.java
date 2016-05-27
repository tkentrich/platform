package platform.component.terrain;

import java.util.ArrayList;
import platform.Dimension;
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
    public void collide(Component c) {
        
    }
    public abstract ArrayList<WalkModifier> walkModifiers();
}
