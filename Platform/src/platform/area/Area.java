package platform.area;

import java.util.ArrayList;
import platform.Dimension;
import platform.Platform;
import platform.component.Component;
import platform.component.terrain.Terrain;

/**
 *
 * @author richkent
 */
public class Area {
    private Dimension size;
    private ArrayList<Terrain> terrain;
    private ArrayList<Component> components;
    private Space[][] space;
    private Dimension spaces;
    
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
        
        spaces = new Dimension(size.x() / Platform.spaceSize.x() + 1, size.y() / Platform.spaceSize.y() + 1);
        
        space = new Space[spaces.x()][spaces.y()];
        // Add terain to spaces
        for (Terrain t : terrain) {
            Dimension start = t.position().dividedBy(Platform.spaceSize);
            Dimension end = t.position().plus(t.size()).dividedBy(Platform.spaceSize);
            for (int y = start.y(); y <= end.y(); y++) {
                if (t.position().plus(t.size()).minus(start.times(Platform.spaceSize)).y() > 0) {
                    for (int x = 0; x <= end.x(); x++) {
                        if (t.position().plus(t.size()).minus(start.times(Platform.spaceSize)).x() > 0) {
                            space[start.x() + x][start.y() + y].add(t);
                        }
                    }
                }
            }
        }
        // Add components to spaces, including one block size away
        addComponents();
        
        findStanding();
    }
    
    public void addComponents() {
        for (Space[] ss : space) {
            for (Space s : ss) {
                s.clearComponents();
            }
        }
        for (Component c : components) {
            Dimension start = c.position().minus(Platform.spaceSize).dividedBy(Platform.spaceSize);
            Dimension end = c.position().plus(c.size()).plus(Platform.blockSize).dividedBy(Platform.spaceSize);
            for (int y = start.y(); y <= end.y() && y < spaces.y(); y++) {
                if (c.position().plus(c.size()).minus(start.times(Platform.spaceSize)).y() > 0) {
                    for (int x = 0; x <= end.x() && x < spaces.x(); x++) {
                        if (c.position().plus(c.size()).minus(start.times(Platform.spaceSize)).x() > 0) {
                            space[start.x() + x][start.y() + y].add(c);
                        }
                    }
                }
            }
        }
    }
    
    public void findStanding() {
        for (Component c : components) {
            c.standing(false);
        }
        for (Space[] ss : space) {
            for (int y = spaces.y() - 0; y >= 0; y--) {
                Space s = ss[y];
                for (Component c : s.components()) {
                    if (c.speed().y() >= 0) { // Skip if component is rising
                        for (Terrain t : s.terrain()) {
                            if (!c.standing() && Math.abs(c.position().plus(c.size()).y() - t.position().y()) < 2) {
                                c.standing(true);
                                c.position().setY(t.position().minus(c.size()).y());
                            }
                        }
                        for (Component c2 : s.components()) {
                            if (!c.standing() && Math.abs(c.position().plus(c.size()).y() - c2.position().y()) < 2) {
                                c.standing(true);
                                c.position().setY(c2.position().minus(c.size()).y());
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void moveAll(int ms) {
        for (Component c : components) {
            c.gravity();
        }
        for (Component c : components) {
            c.position().add(c.speed().times(ms).dividedBy(1000));
        }
        for (Space[] ss : space) {
            for (Space s : ss) {
                ArrayList<Component> c = s.components();
                for (int c1 = 0; c1 < c.size(); c1++) {
                    for (int c2 = c1 + 1; c2 < c.size(); c2++) {
                        
                    }
                }
            }
        }
    }
    
    private boolean collision (Component A, Component B) {
        if (A.position().x() > B.position().plus(B.size()).x() // A to the right of B
         || A.position().y() > B.position().plus(B.size()).y() // A below B
         || A.position().plus(A.size()).x() < B.position().x() // A to the left of B
         || A.position().plus(A.size()).y() < B.position().y() // A above B
           ) {
            return false;
        }
        return true;
    }
    private boolean collision (Component A, Terrain B) {
        if (A.position().x() > B.position().plus(B.size()).x() // A to the right of B
         || A.position().y() > B.position().plus(B.size()).y() // A below B
         || A.position().plus(A.size()).x() < B.position().x() // A to the left of B
         || A.position().plus(A.size()).y() < B.position().y() // A above B
           ) {
            return false;
        }
        return true;
    }
}
