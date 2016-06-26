package platform.component;

import java.util.HashMap;
import java.util.Set;
import platform.component.Pose.Theta;

/**
 *
 * @author richkent
 */
public class PoseLink extends PoseChange {

    private int priority;
    private Pose targetPose;
    private int totalTime;
    private int time;
    private HashMap<Theta, Double> delta;
    
    public PoseLink(int priority, Pose currentPose, Pose targetPose, int time) {
        this.priority = priority;
        this.targetPose = new Pose(targetPose);
        this.totalTime = time;
        delta = new HashMap();
        resetDeltas(currentPose);
        reset();
    }
    
    public Pose targetPose() {
        return new Pose(targetPose);
    }
    
    public double target(Theta t) {
        return targetPose.theta(t);
    }
    
    @Override
    public int priority() {
        return priority;
    }

    @Override
    public boolean active() {
        return time > 0;
    }

    @Override
    public void advance(int ms) {
        time -= ms;
    }

    @Override
    public void resetDeltas(Pose current) {
        for (Theta t : targetPose.keySet()) {
            if (time > 0) {
                delta.put(t, (targetPose.theta(t) - current.theta(t)) / time);
            } else {
                delta.put(t, 0.0);
            }
        }
    }

    @Override
    public Set<Pose.Theta> keySet() {
        return delta.keySet();
    }

    @Override
    public double delta(Pose.Theta t) {
        return delta.get(t);
    }
    
    public void reset() {
        time = totalTime;
    }
    
    @Override
    public String toString() {
        return String.format("Link: %d/%d", time, totalTime);
    }
}
