import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
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
    private ListView<String> onlineUsers;
    @FXML
    private TextField messageField;

    public void initialize(){
        client.setChatArea(chatArea);
        client.setOnlineUsers(onlineUsers);

        onlineUsers.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY)) {
                    if(event.getClickCount() == 2) {
                        selectUser();
                    }
                }
            }
        });
    }

    @FXML
    protected void sendMessage() throws IOException {
        client.sendMessageToServer(messageField.getText());
        messageField.clear();
    }

    @FXML
    protected void selectUser() {
        client.print().println(onlineUsers.getSelectionModel().getSelectedItem());

        onlineUsers.getScene().getStylesheets().add(getClass().getResource("listStyles.css").toExternalForm());
    }

    @FXML
    protected void logOut() throws IOException {

        client.print().println("*QUIT*");

        Stage stage = Main.stage;

        stage.setScene(Main.loginScene);
        stage.show();
    }

}