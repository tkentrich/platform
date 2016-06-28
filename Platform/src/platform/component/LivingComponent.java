package platform.component;

import platform.Dimension;

/**
 *
 * @author richkent
 */
public abstract class LivingComponent extends Component {

    private int health;
    
    public LivingComponent(Dimension position, int health) {
        super(position);
        this.health = health;
    }
    
    public void adjustHealth(int delta) {
        health += delta;
    }
    
    @Override
    public boolean active() {
        return health > 0;
    }
}
