package platform.component;

public class ScoreChange extends CollectResult {
    private int increase;
    public ScoreChange(int i) {
        increase = i;
    }
    public int increase() {
        return increase;
    }
}
