package platform.component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import platform.Dimension;
import platform.Platform;

/**
 *
 * @author richkent
 */
public class Player extends Component {

    @Override
    public int maxFallSpeed() {
        return 96;
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public boolean passable() {
        return true;
    }

    @Override
    public Dimension size() {
        return new Dimension(Platform.blockSize.times(3).dividedBy(3,2));
    }

    @Override
    public BufferedImage image() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int weight() {
        return 100;
    }

    @Override
    public ArrayList<CollisionResult> collide(Component c, Collision.CollisionType type) {
        return null;
    }
    
}
