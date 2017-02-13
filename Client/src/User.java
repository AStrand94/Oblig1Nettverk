import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * Created by stiangrim on 13.02.2017.
 */
public class User {
    private Circle circle;
    private String username;

    public User(Circle circle, String username) {
        this.circle = circle;
        this.username = username;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Paint getColor() {
        return circle.getFill();
    }
}
