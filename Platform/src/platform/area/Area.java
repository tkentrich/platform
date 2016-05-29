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
    private boolean debug;
    
    public Area(Dimension size) {
        this.size = size;
        components = new ArrayList();
        debug = true;
    }
    
    public void debug() {
        debug = !debug;
    }
    public void debug(boolean debug) {
        this.debug = debug;
    }
    
    public ArrayList<Component> components() {
        return components;
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
        
        findStanding();
    }
    
    public void spaceComponents() {
        space.clear();
        
        for (Component c : components) {
            Dimension start = c.position().dividedBy(Platform.spaceSize);
            Dimension end = c.position().plus(c.size()).dividedBy(Platform.spaceSize);
            
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
                if (c.speed().y() <= 0) { // Skip if component is rising
                    for (Component c2 : s.components()) {
                        if (!c.standing() && Math.abs(c.position().plus(c.size()).y() - c2.position().y()) < 2) {
                            c.standing(true);
                            c.position().setY(c2.position().minus(c.size()).y());
                            c.speed().setY(0);
                        }
                    }
                }
            }
        }
    }
    
    public void moveAll(int ms) {
        for (Component c : components) {
            c.gravity(ms);
        }
        for (Component c : components) {
            c.position().add(c.speed().times(ms).dividedBy(1000));
        }
        spaceComponents();
        ArrayList<String> checked = new ArrayList();
        ArrayList<Collision> collisions = new ArrayList();
        for (Space s : space.values()) {
            ArrayList<Component> c = s.components();
            if (debug) {
                System.out.println("Space has " + c.size() + " components");
            }
            for (int c1 = 0; c1 < c.size(); c1++) {
                if (!(c.get(c1) instanceof Terrain || c.get(c1).speed().isZero()) ) {
                    if (debug) {
                        System.out.println("Testing collisions for " + c.get(c1));
                    }
                    for (int c2 = 0; c2 < c.size(); c2++) {
                        if (c1 != c2 &&
                              !(
                                checked.contains(c.get(c1).id() + ":" + c.get(c2).id()) || 
                                checked.contains(c.get(c2).id() + ":" + c.get(c1).id())
                               )
                                ) {
                            checked.add(c.get(c1).id() + ":" + c.get(c2).id());
                            Collision coll = Collision.test(c.get(c1), c.get(c2));
                            if (coll != null) {
                                collisions.add(coll);
                                if (debug) {
                                    System.out.println("Collision: " + coll.description());
                                }
                            }
                        }
                    }
                } else if (debug) {
                    System.out.println(c.get(c1) + " is Terrain and/or not moving, not checking for collisions");
                }
            }
            if (debug && checked.size() > 0) {
                System.out.println("Checked the following for collisions: ");
                for (String str : checked) {
                    System.out.println("  " + str);
                }
            }
        }
        for (Collision coll : collisions) {
            System.out.println("Collision!");
            if (!coll.comp2().passable()) {
                switch (coll.type()) {
                    case NORTH:
                        coll.comp1().position().setY(coll.comp2().position().y() - coll.comp1().size().y());
                        break;
                    case SOUTH:
                        coll.comp1().position().setY(coll.comp2().position().y() + coll.comp2().size().y());
                        break;
                    case EAST:
                        coll.comp1().position().setX(coll.comp2().position().x() + coll.comp2().size().x());
                        break;
                    case WEST:
                        coll.comp1().position().setX(coll.comp2().position().x() - coll.comp1().size().x());
                        break;
                    default:
                }
            }
            coll.comp1().collide(coll.comp2(), coll.type());
            coll.comp2().collide(coll.comp1(), coll.type());
        }
    }
    
}
