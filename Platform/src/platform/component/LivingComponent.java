package platform.component;

import platform.Dimension;

/**
 *
 * @author richkent
 */
public abstract class LivingComponent extends Component {

    private int health;
    
    public LivingComponent(Dimension position) {
        super(position);
    }
    
}
