package platform;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import platform.component.Player;
import platform.component.Player.Pose;
import platform.component.Player.Theta;

/**
 *
 * @author richkent
 */
public class PoseScene implements Observer {
    public Player editing;
    public Player display;
    public Theta theta;
    public ArrayList<Pose> chain;
    public int chainIndex;
    
    public PoseScene() {
        theta = Theta.NECK;
        editing = new Player(new Dimension(300, 200));
        display = new Player(new Dimension(600, 200));
        chain = new ArrayList();
    }
    
    public Player editing() {
        return editing;
    }
    public Theta theta() {
        return theta;
    }
    
    public void adjust(int delta) {
        editing.pose().millis += delta;
    }
    public void adjust(double delta) {
        editing.pose().adjustTheta(theta, delta);
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
    public void resetChain() {
        ArrayList<Pose> newChain = new ArrayList();
        for (Pose p : chain) {
            newChain.add(new Pose(p));
        }
        display.setChain(newChain);
    }
    public void move(int ms) {
        display.move(ms);
    }

    @Override
    public void update(Observable o, Object arg) {
        /*if (o.equals(display)) {
            
        }*/
    }
    
}
