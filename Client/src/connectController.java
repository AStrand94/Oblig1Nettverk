import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.IOException;

public class connectController {

    Client client = Client.getInstance();


    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    @FXML
    private TextField messageField;
    @FXML
    private TextField ipAddress;
    @FXML
    private TextField portNumber;

    @FXML
    private TextArea chatArea;

    @FXML
    private Text errorMessage;
    @FXML
    private Text serverErrorMessage;

    @FXML
    private Button connect;
    @FXML
    private Button send;
    @FXML
    private Button connectButton;

    static Scene chatScene;


    public connectController() throws IOException {

    }

    @FXML
    protected void handleConnectAction(ActionEvent event) {

        if (userName.getText().equals("admin") && password.getText().equals("admin")) {
            try {
                goToChatWindow();
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

    protected void goToChatWindow() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));

        chatScene = new Scene(root, 600, 400);

        Stage stage = (Stage) connect.getScene().getWindow();
        stage.setTitle("Chat");
        stage.setScene(chatScene);
        stage.show();
    }

    @FXML
    protected void sendMessage(ActionEvent event) throws IOException {
        chatArea.appendText(client.sendMessage(messageField.getText()) + "\n");
        messageField.clear();
    }

    @FXML
    protected void setServerButtonPressed(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("setServer.fxml"));

        Scene setServerScene = new Scene(root, 400, 200);

        Stage stage = new Stage();
        stage.setTitle("Chat");
        stage.setScene(setServerScene);
        stage.show();
    }

    @FXML
    protected void setServer(ActionEvent event) throws IOException {

        int port;

        try {
            port = Integer.parseInt(portNumber.getText());
        } catch (NumberFormatException e) {
            serverErrorMessage.setText("Port must be numbers");
            return;
        }

        if (port < 0 ||port > 65535) {
            serverErrorMessage.setText("Ugyldig portnummer (0-65535)");
            return;
        }

        //TODO: Fikse en check for gyldig IP-addresser
        client.setHostName(ipAddress.getText());
        client.setPortNumber(port);


        if(!client.updateSocket())
            serverErrorMessage.setText("Could not make connection");
        else {
            serverErrorMessage.setText("");
            Stage stage = (Stage) connectButton.getScene().getWindow();
            stage.close();
        }


    }

}
