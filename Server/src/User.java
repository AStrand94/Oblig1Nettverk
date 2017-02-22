
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Object for each user in the Server program.
 * Each user is stored as an object.
 */
public class User {

    private StringProperty username;
    private StringProperty password;
    private Circle status;

    /**
     * Constructor of user
     * @param username - Used to log in, and shown to other users
     * @param password - Used to log in, secret
     */
    public User(String username, String password, Circle status){
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.status = status;
    }


    /**
     * @return username of user
     */
    public String getUsername() {
        return username.get();
    }

    public void setUsername(StringProperty username) {
        this.username = username;
    }

    public StringProperty usernameProperty(){
        return username;
    }

    /**
     * @return password of user
     */
    public String getPassword() {
        return password.get();
    }

    public void setPassword(StringProperty password) {
        this.password = password;
    }

    public StringProperty passwordProperty(){
        return password;
    }

    /**
     * @return status of user
     */
    public Circle getStatus() {
        return status;
    }

    /**
     * Sets status of user
     * @param status (online, busy or offline)
     */
    public void setStatus(Circle status) {
        this.status = status;
    }

    public Paint getColor() {
        return status.getFill();
    }
}
