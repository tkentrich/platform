package platform.area;

import java.util.ArrayList;
import java.util.HashMap;
import platform.Dimension;
import platform.Platform;
import platform.component.Collision;
import platform.component.Component;
import platform.component.terrain.Terrain;

/**
 *
 * @author richkent
 */
public class Area {
    private Dimension size;
    private ArrayList<Component> components;
    private HashMap<String, Space> space;
    
    public Area(Dimension size) {
        this.size = size;
        components = new ArrayList();
    }
    
    public void addComponent(Component c) {
        components.add(c);
    }
    
    public void initialize() throws AreaException {
        if (size == null) {
            throw new AreaNotBuiltException("Unknown Size");
        }
        if (components == null) {
            throw new AreaNotBuiltException("No Components");
        }
        
        space = new HashMap();
        spaceComponents();
        
        findStanding();
    }
    
    public void spaceComponents() {
        space.clear();
        
        for (Component c : components) {
            Dimension start = c.position().minus(Platform.spaceSize).dividedBy(Platform.spaceSize);
            Dimension end = c.position().plus(c.size()).plus(Platform.blockSize).dividedBy(Platform.spaceSize);
            for (int y = start.y(); y <= end.y(); y++) {
                if (c.position().plus(c.size()).minus(start.times(Platform.spaceSize)).y() > 0) {
                    for (int x = 0; x <= end.x(); x++) {
                        if (c.position().plus(c.size()).minus(start.times(Platform.spaceSize)).x() > 0) {
                            addToSpace(start.x() + x, start.y() + y, c);
                        }
                    }
                }
            }
        }
        for (String key : space.keySet()) {
            System.out.println(key);
        }
    }
    
    public void addToSpace(int x, int y, Component c) {
        String key = String.format("%d:%d", x, y);
        if (!space.containsKey(key)) {
            space.put(key, new Space());
        }
        space.get(key).add(c);
    }
    
    public void findStanding() {
        for (Component c : components) {
            c.standing(false);
        }
        for (Space s : space.values()) {
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
    
    public void moveAll(int ms) {
        for (Component c : components) {
            c.gravity();
        }
        for (Component c : components) {
            c.position().add(c.speed().times(ms).dividedBy(1000));
        }
        ArrayList<String> checked = new ArrayList();
        ArrayList<Collision> collisions = new ArrayList();
        for (Space s : space.values()) {
            ArrayList<Component> c = s.components();
            for (int c1 = 0; c1 < c.size(); c1++) {
                if (!(c.get(c1) instanceof Terrain)) {
                    for (int c2 = c1 + 1; c2 < c.size(); c2++) {
                        if (c.get(c1).speed().isZero() 
                           && !(checked.contains(c.get(c1).id() + ":" + c.get(c2).id())
                           || checked.contains(c.get(c2).id() + ":" + c.get(c1).id()))) {
                            Collision coll = Collision.test(c.get(c1), c.get(c2));
                            if (coll != null) {
                                collisions.add(coll);
                                System.out.println("Collision: " + coll.description());
                            }
                        }
                    }
                }
            }
        }
    }
    
}
