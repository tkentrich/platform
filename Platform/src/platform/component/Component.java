package platform.component;

import java.awt.Graphics2D;
import java.awt.Polygon;
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
    private int actingFriction;
    private int frictionComponents;
    
    public Component(Dimension position) {
        id = ID++;
        this.position = position.copy();
        this.speed = new Dimension(0);
        standing = false;
        frictionComponents = 0;
        actingFriction = 0;
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
    public void move(int ms) {
        position().add(speed().times(ms).dividedBy(1000));
        if (actingFriction == 0) {
            return;
        }
        int speedReduction;
        if (speed().x() > 0) {
            speedReduction = -actingFriction * ms / 1000;
        } else {
            speedReduction = actingFriction * ms / 1000;
        }
        if (Math.abs(speedReduction) > Math.abs(speed().x())) {
            speedReduction = -speed().x();
        }
        speed().add(speedReduction, 0);
    }
    public void standing(int actingFriction) {
        standing(true);
        this.actingFriction *= frictionComponents++;
        this.actingFriction = (actingFriction + this.actingFriction) / frictionComponents;
    }
    public void standing(boolean standing) {
        this.standing = standing;
        if (standing) {
            speed.setY(0);
        } else {
            actingFriction = 0;
            frictionComponents = 0;
        }
    }
    public boolean standing() {
        return standing;
    }
    public void display(Graphics2D g, Dimension start) {
        g.drawImage(image(), position().minus(start).x(), position().minus(start).y(), size().x(), size().y(), null);
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
    public abstract int friction();
    public abstract ArrayList<CollisionResult> collide(Component c, CollisionType type);

    protected static Polygon polygon(Dimension start, Dimension... delta) {
        int points = delta.length + 1;
        int[] xpoints = new int[points];
        int[] ypoints = new int[points];
        xpoints[0] = start.x();
        ypoints[0] = start.y();
        for (int i = 0; i < delta.length; i++) {
            xpoints[i + 1] = start.plus(delta[i]).x();
            ypoints[i + 1] = start.plus(delta[i]).y();
        }
        return new Polygon(xpoints, ypoints, points);
    }
    protected static Polygon polygon(Dimension start, int... delta) {
        int points = delta.length / 2 + 1;
        int[] xpoints = new int[points];
        int[] ypoints = new int[points];
        xpoints[0] = start.x();
        ypoints[0] = start.y();
        int di = 1;
        for (int i = 0; i < delta.length; i+=2) {
            xpoints[di] = xpoints[di - 1] + delta[i];
            ypoints[di] = ypoints[di - 1] + delta[i+1];
            di++;
        }
        return new Polygon(xpoints, ypoints, points);
    }
}
