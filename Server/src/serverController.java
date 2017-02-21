import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class serverController implements Initializable {

    //@FXML
    //private Text cpuDisplay;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private TextField searchUsername;
    @FXML
    private Button create;
    @FXML
    private Button remove;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User,String> username;
    @FXML
    private TableColumn<User,String> password;
    @FXML
    private TableColumn<User, Circle> status;
    @FXML
    private Button changePortButton;
    @FXML
    private Text statusText;
    @FXML
    private Text portText;

    private String portNumber = "5555";
    private Thread t;

    /**
     * Initializes Server-window.
     * Makes the List searchable by sorting and filtering .
     */
    public void initialize(URL location, ResourceBundle resources) {

        t = Server.startListening(this,portNumber);
        t.start();

        updateStatusText();

        username.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        password.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        FilteredList<User> filteredData = new FilteredList<>(Server.allUsers, p -> true);

        searchUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {

                // If filter text is empty -> display all persons
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getUsername().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches username
                }else return false;
            });
        });

    SortedList<User> sortedData = new SortedList<>(filteredData);

    sortedData.comparatorProperty().bind(userTable.comparatorProperty());

    userTable.setItems(sortedData);
    }

    /**
     * Checks if the thread listening for users is alive, and then sets the server-status
     * to online or offline.
     */
    private void updateStatusText(){
        statusText.setText("Server is : " + (t.isAlive() ? "online" : "offline"));
        portText.setText("Portnumber: " + (t.isAlive() ? portNumber : ""));
    }



    /**
     * Refreshes the table whenever sendUpdatedUSers() is called.
     */
    public void updateTable() {
        userTable.refresh();
    }

    /**
     * Creates a new user in allUsers list
     * The new user cannot exist, username is checked for existence
     */
    public void onCreate() {
        if (usernameInput.getText().trim().isEmpty() || passwordInput.getText().trim().isEmpty())
            setInfoAlertMessage("CreateError", "Fill out the Username and Password fields", "");

        String search = usernameInput.getText();

        if(userExist(search, Server.allUsers)) setInfoAlertMessage("CreateError", "Username already exists", "Choose a different username");


        if(!userExist(search,Server.allUsers)){
            User enterNewU = new User(usernameInput.getText(), passwordInput.getText(), new Circle(8, Color.GRAY));
            Server.allUsers.add(enterNewU);
            userTable.refresh();
            clearInputs();
        }
    }

    /**
     * Removes selected user from allUsers.
     * User cannot be removed if it has active -or busy status.
     */
    public void onRemove() {
        if (userTable.getSelectionModel().getSelectedItem().getColor().equals(Color.GRAY)) {
            Server.allUsers.remove(userTable.getSelectionModel().getSelectedItem());
            clearInputs();
            Server.sendUpdatedUsers();
        } else {
            setInfoAlertMessage("RemoveError", "Not removable!", "Cannot remove this user because it is online");
        }
    }
    /**
     * Clears input-fields
     */
    private void clearInputs() {
        usernameInput.clear();
        passwordInput.clear();
    }

    /**
     * Terminates JavaFX Application
     */
    protected void finalize(){
        Platform.exit();
        System.exit(0);
    }

    /**
     * Information for unwanted actions
     * @param title String, title
     * @param header String, header
     * @param content String, content
     */
    private void setInfoAlertMessage(String title, String header, String content){
        Alert notRemovable = new Alert(Alert.AlertType.INFORMATION);
        notRemovable.setTitle(title);
        notRemovable.setHeaderText(header);
        notRemovable.setContentText(content);
        notRemovable.showAndWait();
    }

    /**
     * Checks if user already exists
     * @param search String, title
     * @param list {@link ObservableList}, list
     */
    private boolean userExist(String search, ObservableList<User> list){
        for (User u : list) {
            if (u.getUsername().trim().equals(search))
                return true;
        }
        return false;
    }

    public void changePort(){
        if (Server.areUsersOnline()){
            System.out.println(Server.areUsersOnline());
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setHeaderText("Users are online, cannot change portnumber! \n" +
                            "Tell users to log off!");
            alert.setTitle("Error");

            alert.showAndWait();
        }else{

            TextInputDialog input = new TextInputDialog(portNumber);
            input.setTitle("Portnumber");
            input.setHeaderText("Input portnumber");

            Optional<String> result = input.showAndWait();

            result.ifPresent(s ->{

                if (s.chars().allMatch(Character::isDigit) && Integer.parseInt(s) < 65536 && !s.equals(portNumber)){
                    t.stop();
                    clearInputs();
                    portNumber = s;
                    t = Server.startListening(this,portNumber);
                    t.start();
                }else{
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Invalid portnumber");
                    a.setHeaderText("Please provide a valid number for portnumber");
                    a.showAndWait();
                    for (char r : s.toCharArray()) System.out.print(r);
                    System.out.println();
                }
            });
            try {
                //Waits for the thread to stop, and then updates the status text
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            updateStatusText();
        }
    }

}


