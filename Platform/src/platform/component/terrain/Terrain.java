package platform.component.terrain;

import java.util.ArrayList;
import platform.component.Component;

/**
 *
 * @author richkent
 */
public abstract class Terrain extends Component {
    public boolean passable() {
        return false;
    }
    public int weight() {
        return 0;
    }
    public abstract ArrayList<WalkModifier> walkModifiers();
}
