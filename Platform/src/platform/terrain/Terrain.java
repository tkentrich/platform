package platform.terrain;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import platform.Dimension;

/**
 *
 * @author richkent
 */
public abstract class Terrain {
    private Dimension position; // BottomLeft corner
    public Dimension position() {
        return position;
    }
    public abstract Dimension size();
    public abstract BufferedImage image();
    public abstract boolean passable();
    public abstract ArrayList<WalkModifier> walkModifiers();
}
