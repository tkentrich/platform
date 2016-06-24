package platform.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author richkent
 */
public class Pose {
    public enum Theta {NECK, FRONT_SHOULDER, FRONT_ELBOW, BACK_SHOULDER, BACK_ELBOW, FRONT_HIP, FRONT_KNEE, BACK_HIP, BACK_KNEE};
    
    private HashMap<Theta, Double> theta;
    
    public Pose(HashMap<Theta, Double> map) {
        theta = new HashMap();
        for (Theta t : map.keySet()) {
            theta.put(t, map.get(t));
        }
    }
    public Pose(Pose original) {
        theta = new HashMap();
        for (Theta t : original.keySet()) {
            theta.put(t, original.theta(t));
        }
    }
    public Set<Theta> keySet() {
        return theta.keySet();
    }
    public double theta(Theta t) {
        return theta.get(t);
    }
    public void adjust(Theta t, double delta) {
        theta.put(t, theta.get(t) + delta);
    }
    public void set(Theta t, double angle) {
        theta.put(t, angle);
    }
    
    public void adjust(int ms, PoseChange ch) {
        if (ch.active()) {
            for (Theta t : ch.keySet()) {
                adjust(t, ch.delta(t));
            }
            ch.advance(ms);
        }
    }
    
    public void adjust(int ms, PoseChange... ch) {
        HashMap<Theta, Double> delta = new HashMap();
        HashMap<Theta, Integer> priority = new HashMap();
        
        for (PoseChange c : ch) {
            if (c != null && c.active()) {
                for (Theta t : c.keySet()) {
                    if (!priority.containsKey(t) || priority.get(t) < c.priority()) {
                        priority.put(t, c.priority());
                        delta.put(t, c.delta(t));
                    }
                }
                c.advance(ms);
            }
        }
        
        for (Theta t : delta.keySet()) {
            adjust(t, delta.get(t) * ms);
        }
    }
    
    public String toString() {
        return String.format("N%1f FS%1f FE%1f BS%1f BE%1f FH%1f FK%1f BH%1f BK%1f [%d]", 
                Math.toDegrees(theta.get(Theta.NECK)), 
                Math.toDegrees(theta.get(Theta.FRONT_SHOULDER)), Math.toDegrees(theta.get(Theta.FRONT_ELBOW)), // frontShldrTheta, frontElbowTheta, 
                Math.toDegrees(theta.get(Theta.BACK_SHOULDER)), Math.toDegrees(theta.get(Theta.BACK_ELBOW)), // backShldrTheta, backElbowTheta, 
                Math.toDegrees(theta.get(Theta.FRONT_HIP)), Math.toDegrees(theta.get(Theta.FRONT_KNEE)), // frontHipTheta, frontKneeTheta, 
                Math.toDegrees(theta.get(Theta.BACK_HIP)), Math.toDegrees(theta.get(Theta.BACK_KNEE))); // backHipTheta, backKneeTheta);
    }

}
