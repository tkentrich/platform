package platform.component;

/**
 *
 * @author richkent
 */
public class ActionComplete {
    public enum Action {FIRE};
    private Action action;
    public ActionComplete (Action a) {
        this.action = a;
    }
    public Action action() {
        return action;
    }
}
