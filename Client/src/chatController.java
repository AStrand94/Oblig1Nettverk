import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
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
        client.sendMessageToServer(messageField.getText());
        messageField.clear();
    }

    @FXML
    protected void selectUser() {
        /*if(onlineUsers.getSelectionModel().getSelectedItem() != null) {
            client.print().println("*QUIT*");
            client.print().println(onlineUsers.getSelectionModel().getSelectedItem());
        }*/

        if(tableView.getSelectionModel().getSelectedItem() != null) {
            ObservableList<User> userSelected;
            userSelected = tableView.getSelectionModel().getSelectedItems();

            //If you try to connect with the one you're already connected to
            if(userSelected.get(0).getUsername().equals(client.getLastConnectedUser())) {
                chatArea.appendText("<You are already connected to " + client.getLastConnectedUser() + ">\n");
            } else {
                client.print().println("*QUIT*");
                client.print().println(userSelected.get(0).getUsername());
            }
        }
    }

}