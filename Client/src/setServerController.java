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

/**
 * Created by stiangrim on 28.01.2017.
 */
public class setServerController {

    Client client = Client.getInstance();

    @FXML
    private TextField ipAddress;
    @FXML
    private TextField portNumber;
    @FXML
    private Text serverErrorMessage;
    @FXML
    private Button connectButton;

    @FXML
    protected void setServer(ActionEvent event) throws IOException {

        int port;

        try {
            port = Integer.parseInt(portNumber.getText());
        } catch (NumberFormatException e) {
            serverErrorMessage.setText("Port must be numbers");
            return;
        }

        if (port < 0 || port > 65535) {
            serverErrorMessage.setText("Invalid portnumber (0-65535)");
            return;
        }

        if (!client.createNewSocket(ipAddress.getText(), port))
            serverErrorMessage.setText("Could not make connection");
        else {
            serverErrorMessage.setText("");
            Stage stage = (Stage) connectButton.getScene().getWindow();
            stage.close();
        }


    }

}
