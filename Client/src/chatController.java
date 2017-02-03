import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by stiangrim on 28.01.2017.
 */
public class chatController {

    Client client = Client.getInstance();

    @FXML
    private TextArea chatArea;
    @FXML
    private TextArea onlineUsers;
    @FXML
    private TextField messageField;

    public void initialize(){
        client.setChatArea(chatArea);
    }
    @FXML
    protected void sendMessage() throws IOException {
        client.sendMessageToServer(messageField.getText());
        messageField.clear();
    }

    @FXML
    protected void logOut() throws IOException {

        client.print().println("*QUIT*");

        Stage stage = Main.stage;

        stage.setScene(Main.loginScene);
        stage.show();
    }

}
