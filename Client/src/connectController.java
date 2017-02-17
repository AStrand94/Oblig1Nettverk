import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controls the connect scene.
 */
public class connectController {

    private Client client = Client.getInstance();

    @FXML
    private Hyperlink backToLoginLink;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    @FXML
    private TextFlow textFlow;
    @FXML
    private Text errorMessage;
    @FXML
    private Text retypePasswordText;
    @FXML
    private PasswordField retypePasswordField;
    @FXML
    private Button logInButton;
    @FXML
    private Button registerButton;

    /**
     *
     * @throws IOException
     */
    public connectController() throws IOException {
        System.out.println(client.read().readLine());
    }

    /**
     * Initializes connect window
     * Tries to log in on ENTER
     */
    public void initialize() {
        userName.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    logIn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });

        password.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    logIn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }

    /**
     * Enables registration nodes, and goes to registration scheme
     */
    @FXML
    protected void registerNewUser() {
        userName.clear();
        password.clear();
        retypePasswordField.clear();
        userName.requestFocus();
        errorMessage.setText("");

        textFlow.setDisable(true);
        textFlow.setOpacity(0);

        retypePasswordText.setDisable(false);
        retypePasswordText.setOpacity(1);

        retypePasswordField.setDisable(false);
        retypePasswordField.setOpacity(1);

        logInButton.setDisable(true);
        logInButton.setOpacity(0);

        registerButton.setDisable(false);
        registerButton.setOpacity(1);

        backToLoginLink.setDisable(false);
        backToLoginLink.setOpacity(1);

        userName.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    registerUser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });

        password.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    registerUser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });

        retypePasswordField.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    registerUser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }

    /**
     * Disables registration nodes, and goes back to logIn-mode
     */
    @FXML
    protected void backToLogin() {
        userName.clear();
        password.clear();
        retypePasswordField.clear();
        errorMessage.setText("");

        textFlow.setDisable(false);
        textFlow.setOpacity(1);

        retypePasswordText.setDisable(true);
        retypePasswordText.setOpacity(0);

        retypePasswordField.setDisable(true);
        retypePasswordField.setOpacity(0);

        logInButton.setDisable(false);
        logInButton.setOpacity(1);

        registerButton.setDisable(true);
        registerButton.setOpacity(0);

        backToLoginLink.setDisable(true);
        backToLoginLink.setOpacity(0);

        userName.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    logIn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });

        password.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    logIn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }

    /**
     * Tries to register a new user, with the given username and password
     * @throws IOException if server returns error
     */
    @FXML
    protected void registerUser() throws IOException {
        //Username contains space
        if (userName.getText().contains(" "))
            errorMessage.setText("Username cannot contains any spaces");
        //Password contains space
        else if (password.getText().contains(" "))
            errorMessage.setText("Password cannot contain any spaces");
        else if (retypePasswordField.getText().equals(""))
            errorMessage.setText("Please retype your password");
        else if (!(password.getText().equals(retypePasswordField.getText())))
            errorMessage.setText("Both password fields must match");
        else if (userName.getText().equals("") && password.getText().equals(""))
            errorMessage.setText("You must enter a username and password..");
        else if (userName.getText().equals(""))
            errorMessage.setText("Please enter a username");
        else if (password.getText().equals(""))
            errorMessage.setText("Please enter a password");
        else {
            client.print().println("n");
            String receivedText = client.read().readLine();
            System.out.println(receivedText);

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
            } else {
                errorMessage.setText("Username is already taken");
            }
        }
    }

    /**
     * Tries to log the user in, with the given username and password
     * @throws IOException if server returns error
     */
    @FXML
    protected void logIn() throws IOException {

        //Confirms to server that a login is about to happen
        client.print().println("y");

        System.out.println(client.read().readLine());

        //Sends username and password to server
        client.print().println(userName.getText() + " " + password.getText());

        //Stores server's response to the login in a String
        String receivedText = client.read().readLine();


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

    /**
     * Opens the chat scene
     * @throws IOException if FXMLLoader returns an error
     */
    private void goToChatWindow() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));

        Scene chatScene = new Scene(root, 600, 400);

        Stage stage = (Stage) logInButton.getScene().getWindow();
        stage.setTitle("Chat");
        stage.setResizable(false);
        stage.setScene(chatScene);

        stage.setOnCloseRequest(event -> {
            client.receiver().interrupt();
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

}
