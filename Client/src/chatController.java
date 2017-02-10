import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
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
    private ListView<String> onlineUsers;
    @FXML
    private TextField messageField;
    @FXML
    private Button connectButton;
    @FXML
    private Text userName;

    public void initialize() {
        client.setChatArea(chatArea);
        client.setOnlineUsers(onlineUsers);
        client.setConnectButton(connectButton);
        userName.setText(client.getUsername());

        onlineUsers.getStylesheets().add(getClass().getResource("listStyles.css").toExternalForm());
    }

    @FXML
    protected void sendMessage() throws IOException {
        client.sendMessageToServer(messageField.getText());
        messageField.clear();
    }

    @FXML
    protected void selectUser() {
        if(onlineUsers.getSelectionModel().getSelectedItem() != null) {
            client.print().println("*QUIT*");
            client.print().println(onlineUsers.getSelectionModel().getSelectedItem());
        }
    }

}