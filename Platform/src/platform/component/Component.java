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
    
    public Component(Dimension position) {
        id = ID++;
        this.position = position.copy();
        this.speed = new Dimension(0);
        standing = false;
    }
    public int id() {
        return id;
    }
    public Dimension position() {
        return position;
    }
    public Dimension speed() {
        // return new Dimension(speed);
        return speed;
    }
    public void push(Dimension force) {
        speed.add(force.dividedBy(weight()));
    }
    public void gravity(int ms) {
        if (maxFallSpeed() > 0 && !standing) {
            push(new Dimension(0, Platform.GRAVITY * 1000 / ms * weight()));
        }
        if (speed.y() > maxFallSpeed()) {
            push(new Dimension(0, (-speed.y() + maxFallSpeed())));
        }
    }
    public void standing(boolean standing) {
        this.standing = standing;
        speed.setY(0);
    }
    public boolean standing() {
        return standing;
    }
    public abstract int maxFallSpeed();
    public abstract boolean visible();
    public abstract boolean passable();
    public abstract Dimension size();
    public abstract BufferedImage image();
    public abstract int weight();
    public abstract void collide(Component c);
}
