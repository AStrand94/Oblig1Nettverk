
/**
 * Created by dusja on 06.02.2017.
 */

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ResourceBundle;
import com.sun.management.OperatingSystemMXBean;


public class serverController implements Initializable {

    @FXML
    private Text cpuDisplay;
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
    private Button refresh;
    @FXML
    TableView<User> userTable;
    @FXML
    TableColumn<User,String> username;
    @FXML
    TableColumn<User,String> password;
    @FXML
    TableColumn<User, Circle> status;

    OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean.class);

    public void initialize(URL location, ResourceBundle resources) {

        //Denne tråden lytter kontinuerlig etter at nye brukere skal koble seg til serveren
        Thread t = Server.startListening();
        t.start();

        username.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        password.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        //server.setTableView(userTable);
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<User> filteredData = new FilteredList<>(Server.allUsers, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        searchUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getUsername().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches first name.
                }else return false; // Does not match.
            });
        });

    // 3. Wrap the FilteredList in a SortedList.
    SortedList<User> sortedData = new SortedList<>(filteredData);

    // 4. Bind the SortedList comparator to the TableView comparator.
    // 	  Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(userTable.comparatorProperty());

    // 5. Add sorted (and filtered) data to the table.
		//server.tableView.setItems(sortedData);
        userTable.setItems(sortedData);
    }

    public void updateTable() {
        userTable.setItems(Server.allUsers);
    }

    public void onCreate(){
        User enterNewU = new User(usernameInput.getText(),passwordInput.getText(),new Circle(8, Color.GRAY));
        if(username != null && password != null) {
            Server.allUsers.add(enterNewU);
            userTable.setItems(Server.allUsers);
            clearInputs();
        }
    }

    public void onRemove() {
        if (userTable.getSelectionModel().getSelectedItem().getColor().equals(Color.GRAY)) {
            Server.allUsers.remove(userTable.getSelectionModel().getSelectedItem());
            clearInputs();
        } else {
                    Alert notRemovable = new Alert(Alert.AlertType.INFORMATION);
                    notRemovable.setTitle("Error");
                    notRemovable.setHeaderText("Not removable!");
                    notRemovable.setContentText("Cannot remove this user because it is online");
                    notRemovable.showAndWait();
            }
    }

    private void clearInputs() {
        usernameInput.clear();
        passwordInput.clear();
    }

    public void cpuInfo(){
        cpuDisplay.setText("hello there");
    }

    protected void finalize(){
        Platform.exit();
        System.exit(0);
    }
}
