package platform.component;

import java.util.ArrayList;
import platform.Dimension;

/**
 *
 * @author richkent
 */
public abstract class Collectible extends Component {

    public Collectible(Dimension position) {
        super(position);
    }
    public abstract ArrayList<CollectResult> collect();
}
