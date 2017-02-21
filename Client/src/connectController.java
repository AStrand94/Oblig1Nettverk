import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by stiangrim on 21.02.2017.
 */
public class connectController {

    Client client = Client.getInstance();

    @FXML
    Pane pane;
    @FXML
    TextField ip;
    @FXML
    TextField port;
    @FXML
    Label errorText;

    @FXML
    void connect() throws IOException {
        if(ip.getText().equals("") || port.getText().equals("")) {
            errorText.setText("Enter ip and port");
            return;
        }

        int portNumber = Integer.parseInt(port.getText());
        if(portNumber < 1 || portNumber > 65535) {
            errorText.setText("Invalid port");
            return;
        }

        Socket socket;
        try {
            socket = new Socket(ip.getText(), portNumber);
        } catch (UnknownHostException e) {
            errorText.setText("Invalid IP");
            return;
        } catch (Exception e) {
            errorText.setText("Could not connect");
            return;
        }

        client.setSocket(socket);
        client.setPrintWriter(new PrintWriter(socket.getOutputStream(), true));
        client.setBufferedReader(new BufferedReader(
                new InputStreamReader(socket.getInputStream())));

        goToLoginScene();
    }

    @FXML
    void quit() {
        client.receiver().interrupt();
        Platform.exit();
        System.exit(0);
    }

    void goToLoginScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));

        Scene loginScene = new Scene(root, 600, 400);
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(loginScene);

        stage.setOnCloseRequest(event -> {
            client.receiver().interrupt();
            Platform.exit();
            System.exit(0);
        });

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();

    }

}
