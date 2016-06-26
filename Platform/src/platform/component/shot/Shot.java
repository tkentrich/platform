package platform.component.shot;

import platform.Dimension;
import platform.component.Component;

/**
 *
 * @author richkent
 */
public abstract class Shot extends Component {

    public Shot(Dimension position, Dimension speed) {
        super(position);
        speed().set(speed);
    }
    
}
