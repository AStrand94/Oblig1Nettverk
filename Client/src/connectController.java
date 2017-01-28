import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class connectController {

    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    @FXML
    private Text errorMessage;

    @FXML
    private Button logInButton;

    static Scene chatScene;


    public connectController() throws IOException {

    }

    @FXML
    protected void logIn(ActionEvent event) {

        if (userName.getText().equals("admin") && password.getText().equals("admin")) {
            try {
                errorMessage.setText("");
                goToChatWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (userName.getText().equals("") && password.getText().equals(""))
                errorMessage.setText("You must enter a username and password..");
            else if (userName.getText().equals(""))
                errorMessage.setText("Please enter a username");
            else if (password.getText().equals(""))
                errorMessage.setText("Please enter a password");
            else
                errorMessage.setText("Wrong username/password");
        }
    }

    @FXML
    protected void registrerUser() {
        System.out.println("registrerUser() clicked");
    }

    @FXML
    protected void openSetServerWindow(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("setServer.fxml"));

        Scene setServerScene = new Scene(root, 400, 200);

        Stage stage = new Stage();
        stage.setTitle("Set server");
        stage.setScene(setServerScene);
        stage.show();
    }

    protected void goToChatWindow() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));

        chatScene = new Scene(root, 600, 400);

        Stage stage = (Stage) logInButton.getScene().getWindow();
        stage.setTitle("Chat");
        stage.setScene(chatScene);
        stage.show();
    }

}
