import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

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
    private Button connect;

    public void initialize(){
        client.setChatArea(chatArea);
        client.setOnlineUsers(onlineUsers);

        onlineUsers.getStylesheets().add(getClass().getResource("listStyles.css").toExternalForm());
    }

    @FXML
    protected void sendMessage() throws IOException {
        client.sendMessageToServer(messageField.getText());
        messageField.clear();
    }

    @FXML
    protected void selectUser() {
        //Hvis man ikke er i en chat
        if(!client.getConnected()) {
            client.print().println(onlineUsers.getSelectionModel().getSelectedItem());
            client.setConnected(true);
            connect.setText("Disconnect");
        }
        //Hvis man allerede er i en chat
        else {
            client.print().write("*QUIT*");
            client.setConnected(false);
            connect.setText("Connect");
        }
    }

}