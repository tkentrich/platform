package platform.component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import platform.Dimension;
import platform.Platform;
import platform.component.Collision.CollisionType;

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
            push(new Dimension(0, Platform.GRAVITY * ms * weight() / 1000));
        }
        if (speed.y() > maxFallSpeed()) {
            push(new Dimension(0, (-speed.y() + maxFallSpeed()) * weight()));
        }
    }
    public void standing(boolean standing) {
        this.standing = standing;
        if (standing) {
            speed.setY(0);
        }
    }
    public boolean standing() {
        return standing;
    }
    public String info() {
        return String.format("%s Pos:%s Size:%s Speed:%s", toString(), position(), size(), speed());
    }
    public String toString() {
        return getClass().getSimpleName() + id;
    }
    public abstract int maxFallSpeed();
    public abstract boolean visible();
    public abstract boolean passable();
    public abstract Dimension size();
    public abstract BufferedImage image();
    public abstract int weight();
    // public abstract void collide(Component c);
    public abstract ArrayList<CollisionResult> collide(Component c, CollisionType type);
}
