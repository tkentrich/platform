package platform.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import platform.Dimension;
import platform.Platform;
import platform.component.Collision;
import platform.component.Component;
import platform.component.Player;
import platform.component.terrain.Terrain;
import static platform.Platform.debug;
import platform.collectible.Collect;
import platform.collectible.ScoreChange;
import platform.component.ActionComplete;
import platform.component.CollisionResult;
import platform.component.Player.PlayerStatus;

/**
 *
 * @author richkent
 */
public class Area extends Observable implements Observer {
    private Dimension size;
    private ArrayList<Component> components;
    private HashMap<String, Space> space;
    private Player player;
    private boolean initialized;
    private Platform game;
    
    public Area(Dimension size, Platform game) {
        this.size = size;
        this.game = game;
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
            Dimension start = c.position().minus(15).dividedBy(Platform.spaceSize);
            Dimension end = c.position().plus(c.size().plus(15)).dividedBy(Platform.spaceSize);
            
            for (int y = start.y(); y <= end.y(); y++) {
                if (c.position().plus(c.size()).minus(start.times(Platform.spaceSize)).y() > 0) {
                    for (int x = start.x(); x <= end.x(); x++) {
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
                if (!(c.speed().y() < 0 || c instanceof Terrain)) { // Skip if component is rising or Terrain
                    for (Component c2 : s.components()) {
                        if (!(c2.passable() || checked.contains(String.format("%s:%s", c, c2)) || c.equals(c2))) {
                            checked.add(String.format("%s:%s", c, c2));
                            int above = Math.abs(c2.position().y() - c.position().plus(c.size()).y());
                            int rightOf = c.position().x() - c2.position().plus(c2.size()).x();
                            int leftOf = c2.position().x() - c.position().plus(c.size()).x();
                            if (above < 2 && rightOf < 0 && leftOf < 0) {
                                // TODO: Percent
                                c.standing(c2.friction());
                                c.position().setY(c2.position().minus(c.size()).y());
                                c.speed().setY(0);
                            }
                        }
                    }
                }
            }
        }
        
        player.checkedStanding();
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
            
            for (int c1 = 0; c1 < c.size(); c1++) {
                if (!(c.get(c1) instanceof Terrain || c.get(c1).speed().isZero()) ) {
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
                            }
                        }
                    }
                }
            }
        }
        ArrayList<CollisionResult> results = new ArrayList();
        for (Collision coll : collisions) {
            // System.out.printf(" Collision between %s and %s direction %s %n", coll.comp1(), coll.comp2(), coll.type().name());
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
            results.addAll(coll.comp1().collide(coll.comp2(), coll.type()));
            results.addAll(coll.comp2().collide(coll.comp1(), coll.type()));
        }
        for (CollisionResult cr : results) {
            if (cr instanceof Collect) {
                components.remove(((Collect)cr).collectible());
            } else if (cr instanceof ScoreChange) {
                game.increaseScore(((ScoreChange)cr).increase());
            } else {
                System.out.println("Unknown CollectionResult!" + cr);
            }
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Player) {
            Player p = (Player)o;
            if (arg != null && arg instanceof ActionComplete) {
                switch (((ActionComplete)arg).action()) {
                    case FIRE:
                        // TODO: Fire Gun
                }
            }
        }
    }
    
    public int score() {
        return game.score();
    }
    public int lives() {
        return game.lives();
    }
}