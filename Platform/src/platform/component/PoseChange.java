package platform.component;

import java.util.Set;
import platform.component.Pose.Theta;

/**
 *
 * @author richkent
 */
public abstract class PoseChange {
    public abstract int priority();
    public abstract boolean active();
    public abstract void advance(int ms);
    public abstract void resetDeltas(Pose current);
    public abstract Set<Theta> keySet();
    public abstract double delta(Theta t);
}
