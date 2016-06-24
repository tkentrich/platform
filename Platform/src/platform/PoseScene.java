package platform;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import platform.component.Player;
import platform.component.Pose;
import platform.component.Pose.Theta;

/**
 *
 * @author richkent
 */
public class PoseScene extends Observable implements Observer {
    public Player editing;
    public Player display;
    public Theta theta;
    public ArrayList<Pose> chain;
    public int chainIndex;
    
    public PoseScene() {
        theta = Theta.NECK;
        editing = new Player(new Dimension(300, 200));
        display = new Player(new Dimension(600, 225));
        chain = new ArrayList();
        chain.add(Player.atRest());
    }
    
    public Player editing() {
        return editing;
    }
    public Theta theta() {
        return theta;
    }
    
    public void adjust(double delta) {
        editing.pose().adjust(theta, delta);
    }
    
    public void nextAngle() {
        theta = Theta.values()[(theta.ordinal()+1) % Theta.values().length]; 
    }
    public void setPose() {
        editing.setPose(chain.get(chainIndex));
    }
    public void nextPose() {
        chainIndex++;
        if (chainIndex >= chain.size()) {
            chainIndex = chain.size() - 1;
        }
        setPose();
    }
    public void prevPose() {
        chainIndex--;
        if (chainIndex < 0) {
            chainIndex = 0;
        }
        setPose();
    }
    public void newPose() {
        chainIndex = chain.size();
        chain.add(Player.zero());
        setPose();
    }
    public void savePose() {
        chain.set(chainIndex, editing.pose());
    }
    public void copyPose() {
        chain.add(new Pose(editing.pose()));
    }
    public void print() {
        for (Pose p : chain) {
            System.out.println(p);
        }
    }
    public void resetChain() {
        System.out.println(System.currentTimeMillis() + " chain reset");
        ArrayList<Pose> newChain = new ArrayList();
        for (Pose p : chain) {
            newChain.add(new Pose(p));
        }
    }
    public void move(int ms) {
        display.move(ms);
        setChanged();
        notifyObservers();
    }

    @Override
    public void update(Observable o, Object arg) {
        /*if (o.equals(display)) {
            
        }*/
    }
    
}
