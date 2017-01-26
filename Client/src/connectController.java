import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.IOException;

public class connectController {

    Client client = new Client();


    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    @FXML
    private TextField messageField;

    @FXML
    private TextArea chatArea;

    @FXML
    private Text errorMessage;

    @FXML
    private Button connect;
    @FXML
    private Button send;

    static Scene chatScene;


    public connectController() throws IOException {

    }

    @FXML
    protected void handleConnectAction(ActionEvent event) {

        if (userName.getText().equals("admin") && password.getText().equals("admin")) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));

                chatScene = new Scene(root, 600, 400);

                Stage stage = (Stage) connect.getScene().getWindow();
                stage.setTitle("Chat");
                stage.setScene(chatScene);
                stage.show();


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if(userName.getText().equals("") && password.getText().equals(""))
                errorMessage.setText("Du må skrive inn et brukernavn og passord..");
            else if(userName.getText().equals(""))
                errorMessage.setText("Du må skrive inn et brukernavn..");
            else if(password.getText().equals(""))
                errorMessage.setText("Du må skrive inn et passord..");
            else
                errorMessage.setText("Feil brukernavn/passord");
        }
    }

    @FXML
    protected void sendMessage(ActionEvent event) throws IOException {
        client.sendMessage(messageField.getText());
        chatArea.appendText(messageField.getText() + "\n");
        messageField.clear();
    }

}
