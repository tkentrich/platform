package platform.component;

import java.awt.image.BufferedImage;
import platform.Dimension;
import platform.Platform;

/**
 *
 * @author richkent
 */
public abstract class Component {
    private int id;
    private static int ID = 0;
    private Dimension position;
    private Dimension speed;
    private boolean standing;
    
    public Component() {
        id = ID++;
    }
    public Dimension position() {
        return position;
    }
    public Dimension speed() {
        return new Dimension(speed);
    }
    public void push(Dimension force) {
        speed.add(force.dividedBy(weight()));
    }
    public void gravity() {
        if (weight() > 0 && !standing) {
            speed.add(0, Platform.GRAVITY);
        }
    }
    public void standing(boolean standing) {
        this.standing = standing;
        speed.setY(0);
    }
    public boolean standing() {
        return standing;
    }
    public abstract boolean visible();
    public abstract boolean passable();
    public abstract Dimension size();
    public abstract BufferedImage image();
    public abstract int weight();
}
