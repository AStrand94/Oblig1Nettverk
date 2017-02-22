import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class serverController implements Initializable {

    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private TextField searchUsername;
    @FXML
    private TextField editUsername;
    @FXML
    private TextField editPassword;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User,String> username;
    @FXML
    private TableColumn<User,String> password;
    @FXML
    private TableColumn<User, Circle> status;
    @FXML
    private MenuItem changePortButton;
    @FXML
    private MenuItem broadcast;
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

        usernameInput.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    onCreate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });

        passwordInput.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    onCreate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });

    }

    /**
     * Checks if the thread listening for users is alive, and then sets the server-status
     * to online or offline.
     */
    public void updateStatusText(){
        statusText.setText(t.isAlive() ? "online" : "offline");
        portText.setText(t.isAlive() ? portNumber : "");
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
    public void onCreate() throws IOException {
        if (usernameInput.getText().trim().isEmpty() || passwordInput.getText().trim().isEmpty()) {
            setInfoAlertMessage("CreateError", "Fill out the Username and Password fields", "");
            return;
        }

        String search = usernameInput.getText();
        String pasw = passwordInput.getText();

        if(userExist(search, Server.allUsers)) setInfoAlertMessage("CreateError", "Username already exists", "Choose a different username");

        if(!isVaibleUsername(search) || !isVaiblePassword(pasw) || search.equals(pasw)) {
            setInfoAlertMessage("CreateError", "Username or Password criteria not fulfilled ",
                    "- Username must be at least 4 characters, without namespaces and different from username password \n " +
                            "- Password must be at least 6 characters, without namespaces and different from the username");
        return;
        }
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
        User u = userTable.getSelectionModel().getSelectedItem();

        if (u != null && u.getColor().equals(Color.GRAY) && Server.checkUser(u.getUsername())) {
            Server.allUsers.remove(userTable.getSelectionModel().getSelectedItem());
            clearInputs();
            Server.sendUpdatedUsers();
        } else {
            if (u == null) setInfoAlertMessage("RemoveError","Select an user!","User must be selected!");
            else setInfoAlertMessage("RemoveError", "Not removable!", "Cannot remove this user because it is online");
        }
    }


    public void editUser(){
        User u = userTable.getSelectionModel().getSelectedItem();

        if (editUsername.getText().trim().isEmpty() || editPassword.getText().trim().isEmpty()) {
            setInfoAlertMessage("EditError", "Fill out the Username and Password fields", "");
            return;
        }

        if (u != null && u.getColor().equals(Color.GRAY) && Server.checkUser(u.getUsername())) {
            u.setUsername(new SimpleStringProperty(editUsername.getText()));
            u.setPassword(new SimpleStringProperty(editPassword.getText()));
            userTable.refresh();
        }
        else{
            if (u == null) setInfoAlertMessage("EditError","Select an user!","User must be selected!");
            else setInfoAlertMessage("EditError", "Not editable!", "Cannot edit this user because it is online");
        }
        clearInputs();
    }

    /**
     * Clears input-fields
     */
    private void clearInputs() {
        usernameInput.clear();
        passwordInput.clear();
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

    private boolean isVaibleUsername(String s){
        if(s.length() >= 4 && !s.contains(" ")) return true;
        else return false;
    }

    private boolean isVaiblePassword(String s){
        if(s.length() >= 6 && !s.contains(" ")) return true;
        else return false;
    }

    /**
     * Lets the user change port - only if there are no online users.
     */
    public void changePort(){
        if (Server.areUsersOnline()){
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
                    try {
                        Server.serverSocket.close();
                    }catch(IOException e){
                        System.out.println("Could not close Serversocket");
                        e.printStackTrace();
                    }
                    t.interrupt();
                    clearInputs();
                    portNumber = s;
                    t = Server.startListening(this,portNumber);
                    t.start();
                }else{
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Invalid portnumber");
                    a.setHeaderText("Please provide a valid number for portnumber");
                    a.showAndWait();
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

    /**
     * Pops up TextInputDialog and lets the user broadcast a message to all clients that are online
     */
    public void broadcastMessages(){

        TextInputDialog input = new TextInputDialog();
        input.setHeaderText("Broadcast message");
        input.setHeaderText("Write a message to broadcast");

        Optional<String> result = input.showAndWait();

        result.ifPresent(Server::broadcaster);
    }

}


