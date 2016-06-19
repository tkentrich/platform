package platform.component;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author richkent
 */
public class PoseChain extends PoseChange {
    
    ArrayList<PoseLink> chain;
    private int resetLink;
    private int chainIndex;
    
    public PoseChain(PoseLink... link) {
        this(0, link);
    }
    
    public PoseChain(int resetLink, PoseLink... link) {
        this.resetLink = resetLink;
        chain = new ArrayList();
        for (PoseLink l : link) {
            chain.add(l);
        }
        chainIndex = 0;
    }

    public void resetChain() {
        for (PoseLink l : chain) {
            l.reset();
        }
        chainIndex = 0;
    }
    
    @Override
    public int priority() {
        return chain.get(chainIndex).priority();
    }

    @Override
    public boolean active() {
        return chainIndex > -1;
    }

    @Override
    public void advance(int ms) {
        chain.get(chainIndex).advance(ms);
        if (!chain.get(chainIndex).active()) {
            System.out.println("Next link");
            Pose ending = chain.get(chainIndex).targetPose();
            chain.get(chainIndex).reset();
            chainIndex++;
            if (chainIndex >= chain.size()) {
                chainIndex = resetLink;
            }
            chain.get(chainIndex).resetDeltas(ending);
        }
    }

    @Override
    public void resetDeltas(Pose current) {
        chain.get(chainIndex).resetDeltas(current);
        System.out.println("Reset Deltas");
    }

    @Override
    public Set<Pose.Theta> keySet() {
        return chain.get(chainIndex).keySet();
    }

    @Override
    public double delta(Pose.Theta t) {
        return chain.get(chainIndex).delta(t);
    }
    
}
