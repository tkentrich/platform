package platform.area;

import java.util.ArrayList;
import platform.component.Component;
import platform.terrain.Terrain;

/**
 *
 * @author richkent
 */
public class Space {
    private ArrayList<Terrain> terrain;
    private ArrayList<Component> components;
    
    public Space() {
        terrain = new ArrayList();
        components = new ArrayList();
    }
    
    public void add(Terrain t) {
        terrain.add(t);
    }
    public void add(Component c) {
        components.add(c);
    }
    public ArrayList<Terrain> terrain() {
        return terrain;
    }
    public ArrayList<Component> components() {
        return components;
    }
    public void clearComponents() {
        components.clear();
    }
}
