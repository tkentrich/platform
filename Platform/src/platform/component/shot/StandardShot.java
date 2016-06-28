package platform.component.shot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import platform.Platform;
import platform.Dimension;
import platform.component.Collision;
import platform.component.CollisionResult;
import platform.component.Component;

/**
 *
 * @author richkent
 */
public class StandardShot extends Shot {

    public static int fireSpeed = Platform.blockSize.x() * 5;
    
    private BufferedImage image;
    private int timeLeft;
    private boolean active;
    
    public StandardShot(Dimension position, Dimension speed) {
        super(position, speed);
        timeLeft = 1000;
        active = true;
    }

    @Override
    public int maxFallSpeed() {
        return 0;
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public boolean passable() {
        return false;
    }

    public static Dimension shotSize() {
        return Platform.blockSize.dividedBy(2);
    }
    
    @Override
    public Dimension size() {
        return shotSize();
    }

    @Override
    public void move(int ms) {
        super.move(ms);
        timeLeft -= ms;
        if (timeLeft <= 0) {
            active = false;
        }
    }
    
    @Override
    public BufferedImage image() {
        if (image == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                image = ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Component/StandardShot.png"));
            } catch (IOException ex) {
            }
        }
        return image;
    }

    @Override
    public int weight() {
        return 0;
    }

    @Override
    public int friction() {
        return 0;
    }

    @Override
    public double frictionFactor() {
        return 0;
    }

    @Override
    public ArrayList<CollisionResult> collide(Component c, Collision.CollisionType type) {
        ArrayList<CollisionResult> toReturn = new ArrayList();
        toReturn.add(new ShotDamage(c, 10));
        return toReturn;
    }
    
    @Override
    public boolean active() {
        return active;
    }
    
}
