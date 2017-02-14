
/**
 * Created by dusja on 06.02.2017.
 */
import com.sun.deploy.util.SessionState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class serverController implements Initializable {

    @FXML
    private TextArea messageArea;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private Button create;
    @FXML
    private Button remove;
    //Defining table
    @FXML
    TableView<UserTable> userTable;
    @FXML
    TableColumn<UserTable,Integer> iID;
    @FXML
    TableColumn<UserTable,String> iUsername;
    @FXML
    TableColumn<UserTable,String> iPassword;
    //@FXML
    //TableColumn<UserTable,String> iStatus;
    @FXML
    TableColumn<UserTable,String> iStatus;

    private int iNumber = 1;


    final ObservableList<UserTable> data = FXCollections.observableArrayList(
            new UserTable(iNumber++,"Name", "test123","online"),
            new UserTable(iNumber++,"Name", "test123","online"),
            new UserTable(iNumber++,"Name", "test123","online")
    );



    public void initialize(URL location, ResourceBundle resources) {

        // creates table data

        iID.setCellValueFactory(new PropertyValueFactory("ID"));
        iUsername.setCellValueFactory(new PropertyValueFactory("Username"));
        iPassword.setCellValueFactory(new PropertyValueFactory<UserTable, String>("Password"));
        iStatus.setCellValueFactory(new PropertyValueFactory<UserTable, String>("Status"));


            userTable.setItems(data);
    }

    public void onCreate(ActionEvent e){
        UserTable enterNewU = new UserTable(iNumber,usernameInput.getText(),passwordInput.getText(),"offline");
        iNumber++;
        data.add(enterNewU);
        clearInputs();
    }

    public void onRemove(ActionEvent e){
        data.remove(userTable.getSelectionModel().getSelectedItem());
        clearInputs();
        iNumber--;

    }

    private void clearInputs() {
        usernameInput.clear();
        passwordInput.clear();
    }
}

/*
  Callback<TableColumn<UserTable, String>, TableCell<UserTable, String>> cellFactory =
                new Callback<TableColumn<UserTable, String>, TableCell<UserTable, String>>() {
                    public TableCell call(TableColumn p) {
                        TableCell cell = new TableCell<UserTable, String>() {
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if(iStatus.getText() == "online") setStyle("-fx-background-color: green");
                                else if(iStatus.getText() == "busy") setStyle("-fx-background-color: orange");
                                else setStyle("-fx-background-color: grey");
                            }

                            private String getString() {
                                return getItem() == null ? "" : getItem().toString();
                            }
                        };


                        return cell;
                    }
                };
 */