package platform.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import platform.Dimension;
import platform.Platform;
import platform.component.Collision;
import platform.component.Component;
import platform.component.Player;
import platform.component.terrain.Terrain;
import static platform.Platform.debug;

/**
 *
 * @author richkent
 */
public class Area extends Observable {
    private Dimension size;
    private ArrayList<Component> components;
    private HashMap<String, Space> space;
    private Player player;
    private boolean initialized;
    
    public Area(Dimension size) {
        this.size = size;
        components = new ArrayList();
        initialized = false;
    }
        
    public ArrayList<Component> components() {
        return components;
    }
    public void addComponent(Component c) {
        components.add(c);
        if (c instanceof Player) {
            player = (Player)c;
        }
    }
    public Player player() {
        return player;
    }
    
    public boolean initialized() {
        return initialized;
    }
    public void initialize() throws AreaException {
        if (size == null) {
            throw new AreaNotBuiltException("Unknown Size");
        }
        if (components == null) {
            throw new AreaNotBuiltException("No Components");
        }
        if (player == null) {
            throw new AreaNotBuiltException("No Player");
        }
        
        space = new HashMap();
        
        initialized = true;
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
                            addToSpace(x, y, c);
                        }
                    }
                }
            }
        }
    }
    
    public Space space(Dimension dim) {
        if (space.containsKey(dim.toString())) {
            return space.get(dim.toString());
        }
        return null;
    }
    public Space space(int x, int y) {
        return space(new Dimension(x, y));
    }
    public void addToSpace(int x, int y, Component c) {
        String key = new Dimension(x, y).toString();
        if (!space.containsKey(key)) {
            space.put(key, new Space());
        }
        space.get(key).add(c);
    }
    
    public void findStanding() {
        for (Component c : components) {
            c.standing(false);
        }
        ArrayList<String> checked = new ArrayList();
        for (Space s : space.values()) {
            for (Component c : s.components()) {
                if (!(c.speed().y() <= 0 || c instanceof Terrain)) { // Skip if component is rising or Terrain
                    for (Component c2 : s.components()) {
                        if (!checked.contains(String.format("%s:%s", c, c2))) {
                            checked.add(String.format("%s:%s", c, c2));
                            if (/*!c.standing() && */
                                    Math.abs(c.position().plus(c.size()).y() - c2.position().y()) < 2 && 
                                    (
                                        c.position().x() < c2.position().plus(c2.size()).x() &&
                                        c.position().plus(c.size()).x() > c2.position().x()
                                    )
                                    ) {
                                // TODO: Percent
                                // System.out.println(c + " is standing on " + c2);
                                c.standing(c2.friction());
                                c.position().setY(c2.position().minus(c.size()).y());
                                c.speed().setY(0);
                            }
                        }
                    }
                } else {
                    // System.out.println(c + " is rising, not checking standing");
                }
            }
        }
    }
    
    public void moveAll(int ms) {
        findStanding();
        for (Component c : components) {
            c.gravity(ms);
        }
        for (Component c : components) {
            c.move(ms);
        }
        player().control();
        
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
                            Collision coll = Collision.test(c.get(c1), c.get(c2), ms);
                            if (coll != null) {
                                collisions.add(coll);
                                if (debug) {
                                    System.out.println("Collision: " + coll.description());
                                }
                            }
                        }
                    }
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
            if (debug) {
                System.out.printf("Collision between %s and %s (%s)%n", coll.comp1(), coll.comp2(), coll.type());
            }
            if (!coll.comp2().passable()) {
                switch (coll.type()) {
                    case NORTH:
                        coll.comp1().position().setY(coll.comp2().position().y() - coll.comp1().size().y());
                        coll.comp1().speed().setY(0);
                        break;
                    case SOUTH:
                        coll.comp1().position().setY(coll.comp2().position().y() + coll.comp2().size().y());
                        coll.comp1().speed().setY(0);
                        break;
                    case EAST:
                        coll.comp1().position().setX(coll.comp2().position().x() + coll.comp2().size().x());
                        coll.comp1().speed().setX(0);
                        break;
                    case WEST:
                        coll.comp1().position().setX(coll.comp2().position().x() - coll.comp1().size().x());
                        coll.comp1().speed().setX(0);
                        break;
                    default:
                }
                if (debug) {
                    System.out.printf("  After collision: %s %n", coll.comp1().info());
                }
            }
            coll.comp1().collide(coll.comp2(), coll.type());
            coll.comp2().collide(coll.comp1(), coll.type());
        }
        setChanged();
        notifyObservers();
    }
    
}
