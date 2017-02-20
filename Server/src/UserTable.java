import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

public class UserTable {

    private final SimpleIntegerProperty rID;
    private final SimpleStringProperty rUsername;
    private final SimpleStringProperty rPassword;
    //private SimpleObjectProperty<Circle> rStatus;
    private final SimpleStringProperty  rStatus;

    public UserTable(int sID, String sUsername, String sPassword, String sStatus) {
        this.rID = new SimpleIntegerProperty(sID);
        this.rUsername = new SimpleStringProperty(sUsername);
        this.rPassword = new SimpleStringProperty(sPassword);
        this.rStatus = new SimpleStringProperty(sStatus);
        //this.rStatus = new SimpleObjectProperty(sStatus);
    }

    public Integer getID(){
        return rID.get();
    }

    public void setID(Integer i){
        rID.set(i);
    }

    public String getUsername(){
        return rUsername.get();
    }

    public void setUsername(String s){
        rUsername.set(s);
    }


    public String getPassword(){
        return rPassword.get();
    }

    public void setPassword(String s){
        rPassword.set(s);
    }

    public String getStatus(){
        return rStatus.get();
    }

    public void setStatus(String s){
        rStatus.set(s);
    }


    /*
    public enum StatCol {
        HIGH(Color.RED), MEDIUM(Color.ORANGE), LOW(Color.BLUE);
        private final Color color;
        private StatCol(Color color) { this.color = color; }
        public Color getColor() { return color; }
    }
*/

}
