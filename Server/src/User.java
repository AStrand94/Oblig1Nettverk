/**
 * Created by brandhaug on 25.01.2017.
 */
public class User {

    private static int id = 1;
    private final int userId;
    private final String userName;
    private final String password;
    private String status;

    public User(String userName, String password){
        this.userId = id++;
        this.userName = userName;
        this.password = password;
        this.status = "offline";
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
