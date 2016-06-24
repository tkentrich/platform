package platform.component.terrain;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import platform.Dimension;
import platform.Platform;

/**
 *
 * @author richkent
 */
public class Dirt extends Terrain {

    //enum DirtType {
    //    A, B, C
    //};
    // private DirtType dt;
    //public static HashMap<DirtType, BufferedImage> images;
    public static BufferedImage image;
    
    public Dirt(Dimension position) {
        super(position);
        /*int rnd = (int)(Math.random() * 3);
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
        }*/
    }
    
    /*public Dirt(Dimension position, DirtType dt) {    
        super(position);
        this.dt = dt;
    }*/
    
    public static void initImages() {
        if (image == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                image = ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Terrain/Photos/Rocks.jpg"));
            } catch (IOException ex) {
                Logger.getLogger(Dirt.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*images = new HashMap();
            try {
            images.put(DirtType.A, ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Terrain/Dirt-A.png")));
            images.put(DirtType.B, ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Terrain/Dirt-B.png")));
            images.put(DirtType.C, ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Terrain/Dirt-C.png")));
            } catch (IOException ex) {
            }*/
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
        if (image == null) {
            initImages();
        }
        return image.getSubimage(position().x(), position().y(), size().x(), size().y());
    }

    @Override
    public boolean passable() {
        return false;
    }

    @Override
    public ArrayList<WalkModifier> walkModifiers() {
        return null;
    }
    
    @Override
    public int maxFallSpeed() {
        return 0;
    }
    
    @Override
    public int friction() {
        return 250;
    }
}
