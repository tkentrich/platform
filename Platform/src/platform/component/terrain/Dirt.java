package platform.component.terrain;

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
public class Dirt extends Terrain {

    enum DirtType {
        A, B, C
    };
    private DirtType dt;
    public static HashMap<DirtType, BufferedImage> images;
    
    public Dirt() {
        int rnd = (int)(Math.random() * 3);
        switch (rnd) {
            default:
            case 0:
                dt = DirtType.A;
                break;
            case 1:
                dt = DirtType.B;
                break;
            case 2:
                dt = DirtType.C;
                break;
        }
        initImages();
    }
    
    public Dirt(DirtType dt) {    
        this.dt = dt;
        initImages();
    }
    
    public static void initImages() {
        if (images == null) {
            images = new HashMap();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                images.put(DirtType.A, ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Terrain/Dirt-A.png")));
                images.put(DirtType.B, ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Terrain/Dirt-B.png")));
                images.put(DirtType.C, ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Terrain/Dirt-C.png")));
            } catch (IOException ex) {
            }
        }
    }
    
    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public Dimension size() {
        return Platform.blockSize;
    }
    
    @Override
    public BufferedImage image() {
        return images.get(dt);
    }

    @Override
    public boolean passable() {
        return false;
    }

    @Override
    public ArrayList<WalkModifier> walkModifiers() {
        return null;
    }
    
}
