package platform.component;

import platform.Dimension;

/**
 *
 * @author richkent
 */
public class Collision {
    public enum CollisionType { NONE, GENERAL, NORTH, SOUTH, EAST, WEST};
    
    /*private enum sideSpec { 
        NORTH(1), SOUTH(2), EAST(3), WEST(4);
        public final int value;
        sideSpec(int value) {
            this.value = value;
        }
    }*/
    
    private static final int NORTH = 1, SOUTH = 2, EAST = 4, WEST = 8;
    
    private Component a, b;
    private CollisionType coll;
    
    public Collision(Component a, Component b, CollisionType coll) {
        this.a = a;
        this.b = b;
        this.coll = coll;
    }
    
    public static Collision test (Component a, Component b) {
        CollisionType coll;
        
        if (a.equals(b)) {
            return null;
        }
        
        if (a.position().x() > b.position().plus(b.size()).x() // A to the right of B
         || a.position().y() > b.position().plus(b.size()).y() // A below B
         || a.position().plus(a.size()).x() < b.position().x() // A to the left of B
         || a.position().plus(a.size()).y() < b.position().y() // A above B
           ) {
            coll = CollisionType.NONE;
        } else {

            coll = CollisionType.GENERAL;
            Dimension aBef1, aBef2, bBef1, bBef2;
            Dimension aAft1, aAft2, bAft1, bAft2;
            aBef1 = a.position().minus(a.speed());
            aBef2 = aBef1.plus(a.size());
            aAft1 = a.position();
            aAft2 = aAft1.plus(a.size());
            bBef1 = b.position().minus(b.speed());
            bBef2 = bBef1.plus(b.size());
            bAft1 = b.position();
            bAft2 = bAft1.plus(b.size());
            
            // A is across the {NORTH|SOUTH|EAST|WEST} boundary  of B
            //   which can also mean all the way across.
            //   I.E. Across the West boundary can mean "Is to the East"
            int before =
                (aBef1.x() < bBef2.x() ? EAST : 0) +
                (aBef2.x() > bBef1.x() ? WEST : 0) +
                (aBef1.y() < bBef2.y() ? SOUTH : 0) +
                (aBef2.y() > bBef1.y() ? NORTH : 0);
            int after = 
                (aAft1.x() < bAft2.x() ? EAST : 0) +
                (aAft2.x() > bAft1.x() ? WEST : 0) +
                (aAft1.y() < bAft2.y() ? SOUTH : 0) +
                (aAft2.y() > bAft1.y() ? NORTH : 0);
            switch (after & ~before) {
                case NORTH:
                    coll = CollisionType.NORTH;
                    break;
                case SOUTH:
                    coll = CollisionType.SOUTH;
                    break;
                case EAST:
                    coll = CollisionType.EAST;
                    break;
                case WEST:
                    coll = CollisionType.WEST;
                    break;
                    // MORE THAN ONE?
            }
        }
        if (coll == CollisionType.NONE) {
            return null;
        }
        return new Collision(a, b, coll);
    }
    
    public Component comp1() {
        return a;
    }
    public Component comp2() {
        return b;
    }
    public CollisionType type() {
        return coll;
    }
    
    public String description() {
        return String.format("Component %s collided %s with Component %s", a.id(), coll.name(), b.id());
    }
}
