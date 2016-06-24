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

    public static BufferedImage image;
    private Dimension size;
    
    public Dirt(Dimension position, Dimension size) {
        super(position);
        this.size = size;
    }
    public Dirt(Dimension position, int xBlocks, int yBlocks) {
        super(position);
        size = new Dimension(xBlocks, yBlocks).times(Platform.blockSize);
    }
    public Dirt(int xStart, int yStart, int xBlocks, int yBlocks) {
        this(new Dimension(xStart, yStart).times(Platform.blockSize), xBlocks, yBlocks);
    }
    
    public static void initImages() {
        if (image == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                image = ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Terrain/Photos/Rocks.jpg"));
            } catch (IOException ex) {
                Logger.getLogger(Dirt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public Dimension size() {
        return size.copy();
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
