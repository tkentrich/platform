package platform.area;

import java.util.ArrayList;
import platform.Dimension;
import platform.Platform;
import platform.component.Component;
import platform.terrain.Terrain;

/**
 *
 * @author richkent
 */
public class Area {
    private Dimension size;
    private ArrayList<Terrain> terrain;
    private ArrayList<Component> components;
    private Space[][] space;
    
    public void initialize() throws AreaException {
        if (size == null) {
            throw new AreaNotBuiltException("Unknown Size");
        }
        if (terrain == null) {
            throw new AreaNotBuiltException("No Terrain");
        }
        if (components == null) {
            throw new AreaNotBuiltException("No Components");
        }
        
        space = new Space[size.x() / Platform.spaceSize.x() + 1][size.y() / Platform.spaceSize.y() + 1];
        // Add terain to spaces
        for (Terrain t : terrain) {
            Dimension start = t.position().dividedBy(Platform.spaceSize);
            Dimension squares = t.size().dividedBy(Platform.spaceSize);
            for (int y = 0; y < squares.y(); y++) {
                if (t.position().plus(t.size()).minus(start.times(Platform.spaceSize)).y() > 0) {
                    for (int x = 0; x < squares.x(); x++) {
                        if (t.position().plus(t.size()).minus(start.times(Platform.spaceSize)).x() > 0) {
                            space[start.x() + x][start.y() + y].add(t);
                        }
                    }
                }
            }
            
        }
    }
    
    public void moveAll() {
        for (Component c : components) {
            c.gravity();
            if (c.speed().y() > 0) { // falling
                
            }
        }
    }
}
