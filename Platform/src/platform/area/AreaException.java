package platform.area;

/**
 *
 * @author richkent
 */
public class AreaException extends Exception {
    private String message;
    
    public AreaException(String message) {
        this.message = message;
    }
    
    public String message() {
        return message;
    }
}
