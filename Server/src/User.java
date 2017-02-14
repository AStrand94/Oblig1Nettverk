/**
 * Created by brandhaug on 25.01.2017.
 */
public class User {

    private static int id = 1;
    private final int userId;
    private final String userName;
    private final String password;
    private String status;

    /**
     * Constructor of user
     * @param userName - Used to log in, and shown to other users
     * @param password - Used to log in, secret
     */
    public User(String userName, String password){
        this.userId = id++;
        this.userName = userName;
        this.password = password;
        this.status = "offline";
    }

    /**
     * @return ID of user
     */
    public int getId() {
        return id;
    }

    /**
     * @return username of user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return status of user
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status of user
     * @param status (online, busy or offline)
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
