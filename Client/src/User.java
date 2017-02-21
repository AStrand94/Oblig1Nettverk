import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class User {
    private Circle circle;
    private String username;

    /**
     * Constructor of user
     * @param circle - Status of user
     * @param username - Username used to log in, and shown to other users
     */
    User(Circle circle, String username) {
        this.circle = circle;
        this.username = username;
    }

    /**
     * @return status of user
     */
    public Circle getCircle() {
        return circle;
    }

    /**
     * Sets status of user
     * @param circle
     */
    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    /**
     * @return username of user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username of user
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Green for online
     * Orange for busy
     * Grey for offline
     * @return the color of the user status
     */
    public Paint getColor() {
        return circle.getFill();
    }
}
