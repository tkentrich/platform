package platform.component;

/**
 *
 * @author richkent
 */
public class Collision {
    public enum CollisionType { NONE, GENERAL, NORTH, SOUTH, EAST, WEST};
    
    private int aI, bI;
    private Component aC, bC;
    private CollisionType coll;
    
    public Collision(Component a, Component b) {
        
        if (a.position().x() > b.position().plus(b.size()).x() // A to the right of B
         || a.position().y() > b.position().plus(b.size()).y() // A below B
         || a.position().plus(a.size()).x() < b.position().x() // A to the left of B
         || a.position().plus(a.size()).y() < b.position().y() // A above B
           ) {
            coll = CollisionType.NONE;
        }
        
    
    }
}
