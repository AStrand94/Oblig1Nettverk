import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Created by stiangrim on 28.01.2017.
 */

/**
 * Controls the chat scene.
 */
public class chatController {

    private Client client = Client.getInstance();

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

    /**
     * Initializes chat window
     */
    public void initialize() {
        client.setChatArea(chatArea);
        client.setConnectButton(connectButton);
        client.setTableView(tableView);
        client.setStatusColumn(statusColumn);
        client.setNameColumn(nameColumn);
        userName.setText(client.getUsername());
    }

    /**
     * If connected, sends a message to the server and clears the message field.
     *
     * @throws IOException if server returns an error
     */
    @FXML
    protected void sendMessage() throws IOException {
        String s = messageField.getText();
        if(s.length() > 0) {
            if (s.charAt(0) == '*') {
                chatArea.appendText("<First character can not be \'*\'>\n");
            } else {
                if(client.getConnected()) {
                    client.print().println(s);
                    messageField.clear();
                } else {
                    chatArea.appendText("<You have to be connected to a user to send a message.>\n");
                }
            }
        }
    }

    /**
     * Sends connectRequest to selected user in the tableView.
     * User must be online.
     */
    @FXML
    protected void selectUser() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            ObservableList<User> userSelected = tableView.getSelectionModel().getSelectedItems();

            if (userSelected.get(0).getColor().equals(Color.GRAY)) {
                chatArea.appendText("<" + userSelected.get(0).getUsername() +
                        " is offline. Please connect to an online user>\n");
                return;
            }

            //If you try to connect with the one you're already connected to
            if (userSelected.get(0).getUsername().equals(client.getLastConnectedUser())) {
                chatArea.appendText("<You are already connected to " + client.getLastConnectedUser() + ">\n");
            } else {
                client.print().println("*QUIT*");
                client.print().println(userSelected.get(0).getUsername());
            }
        } else {
            chatArea.appendText("<Please select a user>\n");
        }
    }

    /**
     * Disconnects from current chat
     * Must be in an active chat
     */
    @FXML
    protected void disconnect() {
        if (client.getConnected()) {
            client.print().println("*QUIT*");
            client.print().println("ok");
        } else {
            chatArea.appendText("<You are not connected to anyone>\n");
        }
    }

}