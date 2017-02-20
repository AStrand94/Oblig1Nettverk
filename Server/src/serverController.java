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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ResourceBundle;
import com.sun.management.OperatingSystemMXBean;


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
    TableView<User> userTable;
    @FXML
    TableColumn<User,String> username;
    @FXML
    TableColumn<User,String> password;
    @FXML
    TableColumn<User, Circle> status;

    /**
     * Initializes Server-window.
     * Makes the List searchable by sorting and filtering .
     */
    public void initialize(URL location, ResourceBundle resources) {

        Thread t = Server.startListening(this);
        t.start();

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

}


