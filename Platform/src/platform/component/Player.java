package platform.component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import platform.Dimension;
import platform.Platform;

/**
 *
 * @author richkent
 */
public class Player extends Component {

    private String status;
    
    private static HashMap<String, BufferedImage> images;
    
    public Player(Dimension position) {
        super(position);
    }

    @Override
    public int maxFallSpeed() {
        return Platform.blockSize.y() * 10;
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public boolean passable() {
        return true;
    }

    @Override
    public Dimension size() {
        return Platform.blockSize.times(3).dividedBy(3,2);
    }

    @Override
    public BufferedImage image() {
        if (images == null) {
            initImages();
        }
        return images.get("Default");
    }

    @Override
    public int weight() {
        return 100;
    }

    @Override
    public ArrayList<CollisionResult> collide(Component c, Collision.CollisionType type) {
        return null;
    }
    
    public static void initImages() {
        if (images == null) {
            images = new HashMap();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                images.put("Default", ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Component/Player/Default.png")));
            } catch (IOException ex) {
            }
        }
    }
}
