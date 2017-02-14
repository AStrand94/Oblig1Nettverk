import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Created by stiangrim on 28.01.2017.
 */
public class chatController {

    Client client = Client.getInstance();

    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;
    @FXML
    private Button connectButton;
    @FXML
    private Text userName;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<User, Circle> statusColumn;
    @FXML
    private TableColumn<User, String> nameColumn;

    public void initialize() {
        client.setChatArea(chatArea);
        client.setConnectButton(connectButton);
        client.setTableView(tableView);
        client.setStatusColumn(statusColumn);
        client.setNameColumn(nameColumn);
        userName.setText(client.getUsername());
    }

    @FXML
    protected void sendMessage() throws IOException {
        String s = messageField.getText();
        if (s.charAt(0) == '*'){
            chatArea.appendText("<First character can not be \'*\'>\n");
        }else {
            client.sendMessageToServer(s);
        }
        messageField.clear();
    }

    @FXML
    protected void selectUser() {
        if(tableView.getSelectionModel().getSelectedItem() != null) {
            ObservableList<User> userSelected;
            userSelected = tableView.getSelectionModel().getSelectedItems();

            if(userSelected.get(0).getColor().equals(Color.GRAY)) {
                chatArea.appendText("<" + userSelected.get(0).getUsername() +
                        " is offline. Please connect to an online user>\n");
                return;
            }

            //If you try to connect with the one you're already connected to
            if(userSelected.get(0).getUsername().equals(client.getLastConnectedUser())) {
                chatArea.appendText("<You are already connected to " + client.getLastConnectedUser() + ">\n");
            } else {
                client.print().println("*QUIT*");
                client.print().println(userSelected.get(0).getUsername());
            }
        } else {
            chatArea.appendText("<Please select a user>\n");
        }
    }

    @FXML
    protected void disconnect() {
        if(tableView.getSelectionModel().getSelectedItem() != null) {
            ObservableList<User> userSelected;
            userSelected = tableView.getSelectionModel().getSelectedItems();

            if (!(userSelected.get(0).getUsername().equals(client.getLastConnectedUser()))) {
                chatArea.appendText("<You are not connected to " + userSelected.get(0).getUsername() + ">\n");
            } else {
                client.setLastConnectedUser("");
                client.print().println("*QUIT*");
                client.print().println("ok");
            }
        } else {
            chatArea.appendText("<Please select a user>\n");
        }
    }

}