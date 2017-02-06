import javafx.application.Platform;
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

    Client client = Client.getInstance();

    String receivedText = "";

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
        System.out.println(client.read().readLine());
    }

    @FXML
    protected void logIn() throws IOException {

        //Confirms to server that a login is about to happen
        client.print().println("y");

        System.out.println(client.read().readLine());

        //Sends username and password to server
        client.print().println(userName.getText() + " " + password.getText());

        //Stores server's response to the login in a String
        receivedText = client.read().readLine();


        //Username and password correct
        if (receivedText.equals("loginAccept")) {
            client.setUsername(userName.getText());
            errorMessage.setText("");
            goToChatWindow();
            receivedText = client.read().readLine();
            System.out.println(receivedText);

            //Server sends message to update users
            if (receivedText.substring(0, 4).equals("*ui*")) {
                client.updateOnlineUsers(receivedText);
            }

            //Starts thread
            client.receiver().start();
            //Username or password incorrect
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

    //Registers a user in the system
    @FXML
    protected void registerUser() throws IOException {
        client.print().println("n");
        String receivedText = client.read().readLine();
        System.out.println(receivedText);

        //Username contains space
        if (userName.getText().contains(" ")) {
            errorMessage.setText("Username cannot contains any spaces");
        }
        //Password contains space
        else if (password.getText().contains(" ")) {
            errorMessage.setText("Password cannot contain any spaces");
        } else {
            client.print().println(userName.getText() + " " + password.getText());

            receivedText = client.read().readLine();
            if (receivedText.equals("loginAccept")) {
                client.setUsername(userName.getText());
                errorMessage.setText("");
                goToChatWindow();
                receivedText = client.read().readLine();
                System.out.println(receivedText);

                //Server sends message to update users
                if (receivedText.substring(0, 4).equals("*ui*")) {
                    client.updateOnlineUsers(receivedText);
                }

                //Starts thread
                client.receiver().start();
            }
        }
    }

    @FXML
    protected void openSetServerWindow() throws IOException {
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

        stage.setOnCloseRequest(event -> {
            client.receiver().interrupt();
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

}
