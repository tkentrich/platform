package platform.area;

import java.util.ArrayList;
import platform.component.Component;
import platform.component.terrain.Terrain;

/**
 *
 * @author richkent
 */
public class Space {
    private ArrayList<Component> components;
    
    public Space() {
        components = new ArrayList();
    }
    
    public void add(Component c) {
        components.add(c);
    }
    public ArrayList<Component> components() {
        return components;
    }
    public void clearComponents() {
        components.clear();
    }
}
