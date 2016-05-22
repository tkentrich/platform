package platform;

/**
 *
 * @author richkent
 */
public class Dimension extends java.awt.Dimension {

    public int x() {
        return width;
    }
    public int y() {
        return height;
    }
    
    public Dimension(int i) {
        this(i, i);
    }

    public Dimension(int x, int y) {
        width = x;
        height = y;
    }
    
    public Dimension(Dimension src) {
        src.copy();
    }
    public Dimension copy() {
        return new Dimension(x(), y());
    }
    public void set(Dimension src) {
        width = src.x();
        height = src.y();
    }
    public void set(int x, int y) {
        width = x;
        height = y;
    }
    public void add(int i) {
        add(i, i);
    }
    public void add(int x, int y) {
        width += x;
        height += y;
    }
    public void add(Dimension addend) {
        width += addend.x();
        height += addend.y();
    }
    public Dimension plus(int i) {
        return plus(i, i);
    }
    public Dimension plus(int x, int y) {
        return new Dimension(x() + x, y() + y);
    }
    public Dimension plus(Dimension addend) {
        return new Dimension(x() + addend.x(), y() + addend.y());
    }
    public Dimension minus(int i) {
        return minus(i, i);
    }
    public Dimension minus(int x, int y) {
        return new Dimension(x() - x, y() - y);
    }
    public Dimension minus(Dimension addend) {
        return new Dimension(x() - addend.x(), y() - addend.y());
    }
    public void multiply(int i) {
        multiply(i, i);
    }
    public void multiply(int x, int y) {
        width *= x;
        height *= y;
    }
    public void multiply(double i) {
        multiply(i, i);
    }
    public void multiply(double x, double y) {
        width *= x;
        height *= y;
    }
    public void multiply(Dimension factor) {
        width *= factor.x();
        height *= factor.y();
    }
    public Dimension times(int i) {
        return times(i, i);
    }
    public Dimension times(int x, int y) {
        return new Dimension(x() * x, y() * y);
    }
    public Dimension times(double i) {
        return times(i, i);
    }
    public Dimension times(double x, double y) {
        return new Dimension((int)(x() * x), (int)(y() * y));
    }
    public Dimension times(Dimension factor) {
        return new Dimension(x() * factor.x(), y() * factor.y());
    }
    public Dimension dividedBy(int i) {
        return dividedBy(i, i);
    }
    public Dimension dividedBy(int x, int y) {
        return new Dimension(x() / x, y() / y);
    }
    public Dimension dividedBy(Dimension divisor) {
        return new Dimension(x() / divisor.x(), y() / divisor.y());
    }
    
    @Override
    public String toString() {
        return String.format("(%d, %d)", width, height);
    }

}
