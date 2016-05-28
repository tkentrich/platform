package platform.collectible;

import java.util.ArrayList;
import platform.Dimension;
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
}
