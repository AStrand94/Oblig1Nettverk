import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private TextField messageField;

    @FXML
    protected void sendMessage(ActionEvent event) throws IOException {
        chatArea.appendText(client.sendMessage(messageField.getText()) + "\n");
        messageField.clear();
    }

    @FXML
    protected void openConnectWindow(ActionEvent event) throws IOException {
        Stage stage = Main.stage;

        stage.setScene(Main.loginScene);
        stage.show();
    }

}
